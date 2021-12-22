structure EcgCalc = EcgCalc

open MLton.PrimThread
open MLton.Thread

fun printit s = print (Int.toString(getMyPriority ())^"] "^s^"\n")

structure Queue:
   sig
      type 'a t

      val new: unit -> 'a t
      val enque: 'a t * 'a -> unit
      val deque: 'a t -> 'a option
   end =
   struct
      datatype 'a t = T of {front: 'a list ref, back: 'a list ref}

      fun new () = T {front = ref [], back = ref []}

      fun enque (T {back, ...}, x) = back := x :: !back

      fun deque (T {front, back}) =
         case !front of
            [] => (case !back of
                      [] => NONE
                    | l => let val l = rev l
                           in case l of
                              [] => raise Fail "deque"
                            | x :: l => (back := []; front := l; SOME x)
                           end)
          | x :: l => (front := l; SOME x)
   end

structure Thread:
   sig
      val exit: unit -> 'a
      val run: unit -> unit
      val spawn: (unit -> unit) -> unit
      val yield: unit -> unit
   end =
   struct
      open MLton
      open Thread

      val topLevel: Thread.Runnable.t option ref = ref NONE

      local
         val threads: Thread.Runnable.t Queue.t = Queue.new ()
      in
         fun ready (t: Thread.Runnable.t) : unit =
            Queue.enque(threads, t)
         fun next () : Thread.Runnable.t =
            case Queue.deque threads of
               NONE => valOf (!topLevel)
             | SOME t => t
      end

            fun 'a exit (): 'a = switch (fn _ => next ())

      fun new (f: unit -> unit): Thread.Runnable.t =
         Thread.prepare
         (Thread.new (fn () => ((f () handle _ => exit ())
                                ; exit ())),
          ())

      fun schedule t = (ready t; next ())

      fun yield (): unit = switch (fn t => schedule (Thread.prepare (t, ())))

      val spawn = ready o new

      fun run(): unit =
         (switch (fn t =>
                  (topLevel := SOME (Thread.prepare (t, ()))
                   ; next()))
          ; topLevel := NONE)
   end

fun forever1 () = while true do ( printit "Ecg: create wave form"; EcgCalc.dorun (); printit "Ecg: do it again" )
fun forever2 () = while true do ( printit "Hi!" )
fun gettime () = get_ticks_since_boot ()

local


in
   val s = gettime();
   val _ = EcgCalc.dorun ();
   val d = (gettime ()) - s;
   val _ = printit (Int.toString(d) ^ " ms");

   val s = gettime();
   val _ = printit "time";
   val _ = EcgCalc.dorun ();
   val d = (gettime ()) - s;
   val _ = printit (Int.toString(d) ^ " ms");

   val s = gettime();
   val _ = EcgCalc.dorun ();
   val d = (gettime ()) - s;
   val _ = printit (Int.toString(d) ^ " ms");

(*
  val _ = print (Int.toString(getMyPriority ())^"] "^Real.toString(EcgCalc.ran1 ())^"\n");
  val f = let in print (Int.toString(getMyPriority ())^"] "^Real.toString(EcgCalc.ran1 ())^"\n") end
  val _ = pspawn (fn () => print (Int.toString(getMyPriority ())^"] Called from user program!\n"), 3)
  val _ = pspawn (fn () => let in print "Hi!\n" end, 2)
  val _ = print "Hello World\n";

  val _ = pspawn (fn () => forever1 (), 4);
  *)

  val _ = forever1 ();
  val _ = Thread.run ()
  val _ = printit "Main end\n"
end
