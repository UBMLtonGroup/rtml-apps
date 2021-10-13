
(* from mlton benchmarks *)


structure Calcs = EcgCalc
exception AssertionFailed

local
    open Array Math

    val printr = print o (Real.fmt (StringCvt.SCI(SOME 8)))
    val printi = print o Int.toString

    val PI = 3.14159265358979323846

    val tpi = 2.0 * PI

    fun assert cond msg = if cond = false then (print (msg^"\n"); raise AssertionFailed) else ()
    fun abs x = if x >= 0.0 then x else ~x

    fun test np =
    let val _ = print "FFT: "
        val real_arr = RealArray.fromList [1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0]
        val imag_arr = RealArray.fromList [0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]
        val real_vec = RealArray.vector real_arr
        val imag_vec = RealArray.vector imag_arr
        val f = Fft.new (RealVector.length real_vec)
        val fr = FftReal.new (RealVector.length real_vec)
        val f_out as (f_re, f_im) = Fft.forward (f, real_vec, imag_vec)
        val i_out as (i_re, i_im) = Fft.inverse (f, f_re, f_im)

        val _ = print "sml lib: pre-fft\n"

        fun loop_i i = if i >= 8 then () else
            (printi i; print "\t";
            printr (RealArray.sub(real_arr, i)); print "\t";
            printr (RealArray.sub(imag_arr, i)); print "\n"; loop_i (i+1))
        val _ = loop_i 0


        val _ = print "sml lib: post-fft\n"
        val _ = Fft.forward_inplace (f, real_arr, imag_arr);

        fun loop_i i = if i >= 8 then () else
            (printi i; print "\t";
            printr (RealArray.sub(real_arr, i)); print "\t";
            printr (RealArray.sub(imag_arr, i)); print "\n"; loop_i (i+1))
        val _ = loop_i 0


        val _ = print "sml lib: post-inverse fft\n"
        val _ = Fft.inverse_inplace (f, real_arr, imag_arr);
        
        fun diva (a, divisor, i) =
            let
            in
               if i < (RealArray.length a) then (
                     RealArray.update (a, i, (Real./(RealArray.sub (a, i), divisor)));
                     diva (a, divisor, i+1)
               ) else ()
            end

        (* still needs to be scaled by 1/N *)
        val _ = diva (real_arr, Real.fromInt(RealArray.length(real_arr)), 0)
        val _ = diva (imag_arr, Real.fromInt(RealArray.length(imag_arr)), 0)

        fun loop_i i = if i >= 8 then () else
            (printi i; print "\t";
            printr (RealArray.sub(real_arr, i)); print "\t";
            printr (RealArray.sub(imag_arr, i)); print "\n"; loop_i (i+1))
        val _ = loop_i 0

    in
        print "\n"
    end
in
    val _ = test 8;
end
