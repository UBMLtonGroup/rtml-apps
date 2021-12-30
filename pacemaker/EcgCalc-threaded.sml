structure EcgCalc = 
struct
   open Array Math List
   open MLton.PrimThread

   val PI = 3.14159265358979323846

   val printr = print o (Real.fmt (StringCvt.SCI(SOME 14)))
   val printi = print o Int.toString
   fun power b e : real = if e = 0 then 1.0 else b * power b (e-1);

   fun ran1 () = 
   let
      val r = MLton.Real.fromWord (MLton.Random.rand ())
      val max = Word.wordSize
      val mx = (power 2.0 max) - 1.0
   in
      r / mx
   end;

   fun stdevl (x : real list,  n : real) = 
   let
      fun sumListRec [] : real  = 0.0
         | sumListRec (x::xs) = x + (sumListRec xs)

      val sumOfList = sumListRec x

      fun len(xs) : real = case xs of
         [] => 0.0
         | (_::xs') => 1.0 + len(xs')

      val lenOfList = len(x)
      val mean = sumOfList / lenOfList

      fun diff (xs, total, mean) = case xs of
            [] => total
         | (x::xs') => diff (xs', (total+((x - mean) * (x - mean))), mean)

      val total = diff (x, 0.0, mean)

      (*
      val _ = print ("Sum:   "^Real.toString(sumOfList)^"\n")
      val _ = print ("Len:   "^Real.toString(lenOfList)^"\n")
      val _ = print ("Mean:  "^Real.toString(mean)^"\n")
      val _ = print ("Total: "^Real.toString(total)^"\n")
      *)
   in
      Math.sqrt (total / (n-1.0))
   end

   fun stdeva (x : real array,  n : real) =
   let
      fun sumArrayRec (arr : real array, s : real, n : int) =
            if n >= Array.length(arr) then s else sumArrayRec (arr, (Array.sub(arr, n)+s), (n+1))

      val sumOfList = sumArrayRec (x, 0.0, 0)
      val lenOfList = Array.length(x)
      val mean = sumOfList / Real.fromInt(lenOfList)

      fun diff (arr, n, total, mean) =
         if n >= Array.length(arr) then total else 
         diff (arr, (n+1), 
               (total + ( (Array.sub(arr, n) - mean) * (Array.sub(arr, n) - mean) )
               ), mean)
      
      val total = diff (x, 0, 0.0, mean)
      (*
      val _ = print ("Sum:   "^Real.toString(sumOfList)^"\n")
      val _ = print ("Len:   "^Int.toString(lenOfList)^"\n")
      val _ = print ("Mean:  "^Real.toString(mean)^"\n")
      val _ = print ("Total: "^Real.toString(total)^"\n")
      *)
   in
      Math.sqrt (total / (n-1.0))
   end


   (* the rest is a straight port of the java version. 1-relative arrays
      are converted to zero relative *)

   fun rrprocess(rr: real array, flo: real, fhi: real,
            flostd: real, fhistd: real, lfhfratio: real,
            hrmean: real, hrstd: real, sf: real, n: int) =
   let
      val w = array(n+1, 0.0)
      val Hw = array(n+1, 0.0)
      val Sw = array(n+1, 0.0)
      (* ph0= new double[(int)(n/2-1 +1)]; *)
      val len_ = Real.trunc((Real.fromInt(n) / 2.0) - 1.0 + 1.0)
      val ph0 = array(len_, 0.0)
      val ph = array(n, 0.0)
      val SwC = array((2*n)+1, 0.0)

      val w1 = 2.0 * PI * flo
      val w2 = 2.0 * PI * fhi
      val c1 = 2.0 * PI * flostd
      val c2 = 2.0 * PI * fhistd
      val sig2 = 1.0
      val sig1 = lfhfratio
      val rrmean = 60.0 / hrmean
      val rrstd = 60.0 * hrstd/(hrmean*hrmean)

      (* df = sf/(double)n;
         for(i=1; i<=n; i++)
           w[i] = (i-1)*2.0*PI*df;

         array changed to zero relative
      *)
      val df = sf / Real.fromInt(n)

      val i = ref 0
      val _ = while (!i) < n do (
         update(w, !i, Real.fromInt(!i) * 2.0 * PI * df);
         i := !i + 1
      )

      val i = ref 0
      val _ = while (!i) < n do (
         update(Hw, !i, 
            (sig1 * Math.exp(~0.5*(Math.pow(sub(w, !i)-w1, 2.0) / Math.pow(c1, 2.0))) / Math.sqrt(2.0*PI*c1*c1)) +
            (sig2 * Math.exp(~0.5*(Math.pow(sub(w, !i)-w2, 2.0) / Math.pow(c2, 2.0))) / Math.sqrt(2.0*PI*c2*c2))
         );
         i := !i + 1
      )

      (*  for(i=1; i<=n/2; i++)
           Sw[i] = (sf/2.0)* Math.sqrt(Hw[i]w);
           *)
      val i = ref 0
      val _ = while (!i) < Real.trunc(Real.fromInt(n)/2.0) do (
         update(Sw, !i, (sf/2.0) * Math.sqrt(sub(Hw, !i)));
         i := !i + 1
      )

      (*    for(i=n/2+1; i<=n; i++)
           Sw[i] = (sf/2.0)* Math.sqrt(Hw[n-i+1]);
      *)
      val i = ref (Real.trunc(Real.fromInt(n)/2.0) + 1)
      val _ = while (!i) < n do (
         update(Sw, !i, (sf/2.0) * Math.sqrt(sub(Hw, (n-(!i)))));
         i := !i + 1
      )

      (*
              /* randomise the phases */
        for(i=1; i<=n/2-1; i++)
           ph0[i] = 2.0*PI*ran1();
      *)
      val i = ref 0
      val _ = while (!i) < Real.trunc(Real.fromInt(n)/2.0) do (
         update(ph0, !i, 2.0 * PI * (ran1()));
         i := !i + 1
      )

      (*
        ph[1] = 0.0;
        for(i=1; i<=n/2-1; i++)
           ph[i+1] = ph0[i];
      *)
      val i = ref 0
      val _ = update(ph, 0, 0.0)
      val _ = while (!i) < (Real.trunc(Real.fromInt(n)/2.0)-1) do (
         update(ph, (!i)+1, sub(ph0, !i));
         i := !i + 1
      )

      (*
        ph[n/2+1] = 0.0;
        for(i=1; i<=n/2-1; i++)
           ph[n-i+1] = - ph0[i]; 
      *)
      val i = ref 0
      val _ = update(ph, Real.trunc(Real.fromInt(n)/2.0), 0.0)
      val _ = while (!i) < (Real.trunc(Real.fromInt(n)/2.0)-1) do (
         update(ph, n-(!i)-1, Real.~(sub(ph0, !i)));
         i := !i + 1
      )

      (*
        /* make complex spectrum */
        for(i=1; i<=n; i++)
           SwC[2*i-1] = Sw[i]* Math.cos(ph[i]);
      *)
      val i = ref 0
      val _ = while (!i) < n do (
         update(SwC, 2*(!i), sub(Sw, !i) * Math.cos(sub(ph, !i)));
         i := !i + 1
      )
      (*
        for(i=1; i<=n; i++)
           SwC[2*i] = Sw[i]* Math.sin(ph[i]);
      *)
      val i = ref 0
      val _ = while (!i) < n do (
         update(SwC, 2*(!i)+1, sub(Sw, !i) * Math.sin(sub(ph, !i)));
         i := !i + 1
      )

      (*
         /* calculate inverse fft */
        ifft(SwC,n,-1);

        SwC is a list of tuples representing complex numbers (real, imag)
        and Fft wants them as separate arrays, so we split it apart below.
      *)

      val half = n div 2
      val real_arr = RealArray.array(half, 0.0)
      val imag_arr = RealArray.array(half, 0.0)
      val i = ref 0 
      val _ = while (!i) < half do (
         RealArray.update(real_arr, !i, sub(SwC, 2*(!i)));
         RealArray.update(imag_arr, !i, sub(SwC, 2*(!i)+1));
         i := !i + 1
      )
      val f = Fft.new (half)
      val _ = Fft.inverse_inplace (f, real_arr, imag_arr);

      (*
        /* extract real part */
        for(i=1; i<=n; i++)
           rr[i] = (1.0/(double)n)*SwC[2*i-1];
      *)
      val i = ref 0
      val _ = while (!i) < half do (
         update(rr, 2*(!i), (1.0 / Real.fromInt(n)) * RealArray.sub(real_arr, !i));
         i := !i + 1
      )

      (*
        xstd = stdev(rr,n);
        ratio = rrstd/xstd; 
      *)
      val xstd = stdeva(rr, Real.fromInt(n))
      val ratio = rrstd / xstd
      
      (*
        for(i=1; i<=n; i++)
           rr[i] *= ratio;

        for(i=1; i<=n; i++)
           rr[i] += rrmean;
      *)
      val i = ref 0
      val _ = while (!i) < n do (
         update(rr, !i, sub(rr, !i) * ratio);
         i := !i + 1
      )

      val i = ref 0
      val _ = while (!i) < n do (
         update(rr, !i, sub(rr, !i) + rrmean);
         i := !i + 1
      )

   in
      ()
   end

   fun dorun (locknum, pos, buffer) = let
      val NR_END = 1.0
      val IA = 16807.0
      val IM = 2147483647.0
      val AM = 1.0/IM
      val IQ = 127773.0
      val IR = 2836.0
      val NTAB = 32.0
      val NDIV = 1.0 + (IM-1.0)/NTAB
      val EPS = 1.2e~7
      val RNMX = 1.0-EPS
      val Necg = 0 (* number of ECG outputs *)
      val mstate = 3 (* system state space dimension *)
      val xinitial = 1.0 (* initial x co-ordinate value *)
      val yinitial = 0.0 (* initial y co-ordinate value *)
      val zinitial = 0.04 (* initial z co-ordinate value *)

      val tpi = 2.0 * PI
      
      (* q = Math.rint(paramOb.getSf()/paramOb.getSfEcg()); *)
      val sf_ = Real.fromInt(EcgParam.getSf())
      val sfecg_ = Real.fromInt(EcgParam.getSfEcg())
      val q = Real.round(sf_ / sfecg_)

      (* qd = (double)paramOb.getSf()/(double)paramOb.getSfEcg(); *)
      val qd = sf_ / sfecg_

      (*  convert angles from degrees to radians and copy a vector to ai
        for(i=1; i <= 5; i++){
            ti[i] = paramOb.getTheta(i-1) * PI/180.0;
            ai[i] = paramOb.getA(i-1);
        }
      *)

      val PIdiv180 = PI / 180.0
      val thetas = EcgParam.getTheta ()
      val As = EcgParam.getA ()
      val ti = Array.fromList([
         sub(thetas, 0) * PIdiv180,
         sub(thetas, 1) * PIdiv180,
         sub(thetas, 2) * PIdiv180,
         sub(thetas, 3) * PIdiv180,
         sub(thetas, 4) * PIdiv180
      ])

      val ai = Array.fromList([
         sub(As, 0),
         sub(As, 1),
         sub(As, 2),
         sub(As, 3),
         sub(As, 4)
      ])

      (* adjust extrema parameters for mean heart rate
        hrfact =  Math.sqrt(paramOb.getHrMean()/60);
        hrfact2 = Math.sqrt(hrfact);
      *)

      val hrfact = Math.sqrt((EcgParam.getHrMean()) / 60.0)
      val hrfact2 = Math.sqrt(hrfact)

      (* 
        for(i=1; i <= 5; i++)
           bi[i] = paramOb.getB(i-1) * hrfact;
      *)
      val Bs = EcgParam.getB ()
      val bi = Array.fromList([
         sub(Bs, 0) * hrfact,
         sub(Bs, 1) * hrfact,
         sub(Bs, 2) * hrfact,
         sub(Bs, 3) * hrfact,
         sub(Bs, 4) * hrfact
      ])

      (*
        ti[1] *= hrfact2;
        ti[2] *= hrfact;
        ti[3] *= 1.0;
        ti[4] *= hrfact;
        ti[5] *= 1.0;
      *)
      val _ = update(ti, 0, sub(ti, 0) * hrfact2)
      val _ = update(ti, 1, sub(ti, 1) * hrfact)
      val _ = update(ti, 2, sub(ti, 2) * 1.0)
      val _ = update(ti, 3, sub(ti, 3) * hrfact)
      val _ = update(ti, 4, sub(ti, 4) * 1.0)

      (* state vector
              x= new double[4];
      *)
      val x = Array.array(4, 0.0)

      (* initialize vector *)
      val _ = update(x, 0, xinitial)
      val _ = update(x, 1, yinitial)
      val _ = update(x, 2, zinitial)

      val rseed = ~(EcgParam.getSeed())

      (* calculate time scales *)
      val h = 1.0 / sf_
      val tstep = 1.0 / sfecg_

      (* calculate the length of the RR time series 
        rrmean = (60.0/paramOb.getHrMean());
        Nrr=(int)Math.pow(2.0, Math.ceil(Math.log(paramOb.getN()*rrmean*paramOb.getSf())/Math.log(2.0))); 
      *)
      val rrmean = 60.0 / (EcgParam.getHrMean())
      val N_as_real = Real.fromInt(EcgParam.getN())
      val N_mean_Sf = N_as_real * rrmean * sf_
      val Nrr = Real.trunc(Math.pow(2.0, Real.fromInt(Real.ceil(Math.log10( N_mean_Sf ) / Math.log10(2.0) ))))

      (* create rrprocess with required spectrum   *)
      val rr = Array.array(Nrr+1, 0.0)
      val _ = rrprocess(
               rr, 
               EcgParam.getFLo(),
               EcgParam.getFHi(),
               EcgParam.getFLoStd(),
               EcgParam.getFHiStd(),
               EcgParam.getLfHfRatio(),
               EcgParam.getHrMean(),
               EcgParam.getHrStd(),
               sf_, 
               Nrr
            )
      
        (*
        /* create piecewise constant rr */
        rrpc = new double[(2*Nrr) + 1];
        tecg = 0.0;
        i = 1;
        j = 1;
        while(i <= Nrr){  
          tecg += rr[j];
          j = (int) Math.rint(tecg/h);
          for(k=i; k<=j; k++)
              rrpc[k] = rr[i];
          i = j+1;
        }
        Nt = j;
        *)
        
      val rrpc = Array.array(2*Nrr, 0.0)
      val tecg = ref 0.0
      val i = ref 0
      val j = ref 0
      val k = ref 0

      val _ = while !i < Nrr do (
         tecg := !tecg + sub(rr, !j);
         j := Real.round((!tecg)/h);
         k := !i;
         while !k < !j do (
            update(rrpc, !k, sub(rr, !i));
            k := !k + 1
         );
         i := !j + 1
      )
      val Nt = !j

      (*     private void derivspqrst(double t0,double[] x, double[] dxdt){ *)
      fun derivspqrst (t0 : real, x: real array, dxdt: real array) =
      let
      (*
         k = 5; 
         xi = new double[k + 1];
         yi = new double[k + 1];
         w0 = angfreq(t0);
         r0 = 1.0; x0 = 0.0;  y0 = 0.0;  z0 = 0.0;
         a0 = 1.0 - Math.sqrt((x[1]-x0)*(x[1]-x0) + (x[2]-y0)*(x[2]-y0))/r0;
         *)
         val k = 5
         val xi = array(k, 0.0)
         val yi = array(k, 0.0)

            (*
            instead of angfreq, just inline it...
            h and rrpc are local to dorun/main and must be passed in

            angfreq(double t):
               int i = 1 + (int)Math.floor(t/h);
               return(2.0*PI/rrpc[i]);
            w0 = angfreq(t0);

            is: 

            i = 1 + Math.floor(t0/h)
            w0 = 2.0 * PI / rrpc[i]
            *)

         val i = 0 + Real.floor(t0/h)
         val w0 = 2.0 * PI / sub(rrpc, i)

         val r0 = 1.0
         val x0 = 0.0
         val y0 = 0.0
         val z0 = 0.0
         val a0 = 1.0 - Math.sqrt(
            (sub(x, 0)-x0)*(sub(x, 0)-x0) + (sub(x, 1)-y0)*(sub(x, 1)-y0)/r0
            )
         
         (*
         for(i=1; i<=k; i++)
               xi[i] = Math.cos(ti[i]);
         for(i=1; i<=k; i++)
               yi[i] = Math.sin(ti[i]);   
         *)
         val i = ref 0 
         val _ = while (!i) < k do (
            update(xi, !i, Math.cos(sub(ti, !i)));
            i := !i + 1
         )
         val i = ref 0
         val _ = while (!i) < k do (
            update(yi, !i, Math.sin(sub(ti, !i)));
            i := !i + 1
         )

         (*
            zbase = 0.005* Math.sin(2.0*PI*paramOb.getFHi()*t0);
            *)
         val zbase = 0.005 * Math.sin(2.0 * PI * (EcgParam.getFHi()) * t0)

         (*
            t = Math.atan2(x[2],x[1]);
            dxdt[1] = a0*(x[1] - x0) - w0*(x[2] - y0);
            dxdt[2] = a0*(x[2] - y0) + w0*(x[1] - x0); 
            dxdt[3] = 0.0;  
         *)
         val t = Math.atan2(sub(x, 1), sub(x, 0))
         val _ = update(dxdt, 0, a0 * (sub(x, 0)-x0) - w0*(sub(x, 1)-y0))
         val _ = update(dxdt, 1, a0 * (sub(x, 1)-y0) + w0*(sub(x, 0)-x0))
         val _ = update(dxdt, 2, 0.0)

         (*
            for(i=1; i<=k; i++){
               dt = Math.IEEEremainder(t-ti[i], 2.0*PI);
               dt2 = dt*dt;
               dxdt[3] += -ai[i] * dt * Math.exp(-0.5*dt2/(bi[i]*bi[i])); 
            }
            dxdt[3] += -1.0*(x[3] - zbase);
        *)
         val i = ref 0 
         val _ = while (!i) < k do (
            let
               val dt = Real.rem(t - sub(ti, !i), 2.0*PI)
               val dt2 = dt * dt
            in (
               update(dxdt, 2, sub(dxdt, 2) + (~1.0*sub(ai, !i)*dt*Math.exp(~0.5*dt2/(sub(bi, !i)*sub(bi, !i)))));
               i := !i + 1
            ) end
         )
         val _ = update(dxdt, 2, sub(dxdt, 2) + (~1.0*(sub(x, 2)-zbase)))
      in
         ()
      end

      fun Rk4 (y : real array, n : int, x : real, h : real, yout : real array) = 
      let
      (*
         dydx=  new double[n + 1];
         dym =  new double[n + 1];
         dyt =  new double[n + 1];
         yt  =  new double[n + 1];

         hh= h * 0.5;
         h6= h/6.0;
         xh= x + hh;
      *)
         val dydx = array(n, 0.0)
         val dym = array(n, 0.0)
         val dyt = array(n, 0.0)
         val yt = array(n, 0.0)
         val hh = h * 0.5
         val h6 = h / 6.0
         val xh = x + hh


         (*
         derivspqrst(x,y,dydx);  
         for (i=1; i<=n; i++)
               yt[i]=y[i]+hh*dydx[i];
         *) 
         val _ = derivspqrst(x, y, dydx)
         val i = ref 0
         val _ = while (!i) < n do (
            update(yt, !i, sub(y, !i) + hh * sub(dydx, !i));
            i := !i + 1
         )

         (* 
         derivspqrst(xh,yt,dyt);
         for (i=1; i<=n; i++)
               yt[i]=y[i] + hh * dyt[i];
         *)
         val _ = derivspqrst(xh, yt, dyt)
         val i = ref 0
         val _ = while (!i) < n do (
            update(yt, !i, sub(y, !i) + hh * sub(dyt, !i));
            i := !i + 1
         )

         (*
         derivspqrst(xh,yt,dym);
         for (i=1; i<=n; i++){
                  yt[i]=y[i] + h * dym[i];
                  dym[i] += dyt[i];
         }
         *)
         val _ = derivspqrst(xh, yt, dym)
         val i = ref 0
         val _ = while (!i) < n do (
            update(yt, !i, sub(y, !i) + h * sub(dym, !i));
            update(dym, !i, sub(dym, !i) + sub(dyt, !i));
            i := !i + 1
         )

         (*
         derivspqrst(x+h,yt,dyt);
         for (i=1; i<=n; i++)
                  yout[i]=y[i] + h6 * (dydx[i]+dyt[i]+2.0*dym[i]);
         *)
         val _ = derivspqrst(x+h, yt, dyt)
         val i = ref 0
         val _ = while (!i) < n do (
            update(yout, !i, sub(y, !i) + h6 * (sub(dydx, !i) + sub(dyt, !i) + 2.0*sub(dym, !i)));
            i := !i + 1
         )

      in
         ()
      end

        (*
         /* integrate dynamical system using fourth order Runge-Kutta*/
        xt = new double[Nt + 1];
        yt = new double[Nt + 1];
        zt = new double[Nt + 1];
        timev = 0.0;
        for(i=1; i<=Nt; i++){
            xt[i] = x[1];
            yt[i] = x[2];
            zt[i] = x[3];
            Rk4(x, mstate, timev, h, x);
            timev += h;
        }
        *)

      val xt = array(Nt, 0.0)
      val yt = array(Nt, 0.0)
      val zt = array(Nt, 0.0)
      val timev = ref 0.0
      val i = ref 0
      val _ = while (!i) < Nt do (
         update(xt, !i, sub(x, 0));
         update(yt, !i, sub(x, 1));
         update(zt, !i, sub(x, 2));
         Rk4(x, mstate, !timev, h, x);
         timev := !timev + h;
         i := !i + 1
      )

      (*
        /* downsample to ECG sampling frequency */
        xts = new double[Nt + 1];
        yts = new double[Nt + 1];
        zts = new double[Nt + 1];

        j=0;
        for(i=1; i<=Nt; i+=q){ 
          j++;
          xts[j] = xt[i];
          yts[j] = yt[i];
          zts[j] = zt[i];
        }
        Nts = j;
        *)
      val xts = array(Nt, 0.0)
      val yts = array(Nt, 0.0)
      val zts = array(Nt, 0.0)
      val j = ref 0
      val i = ref 0

      val _ = while (!i) < Nt do (
         update(xts, !j, sub(xt, !i));
         update(yts, !j, sub(yt, !i));
         update(zts, !j, sub(zt, !i));
         j := !j + 1;
         i := !i + q
      )
      val Nts = !j

      fun detectpeaks (ipeak: real array, x: real array, y: real array, z: real array, n: int) = 
      let
         (*
            thetap1 = ti[1];
            thetap2 = ti[2];
            thetap3 = ti[3];
            thetap4 = ti[4];
            thetap5 = ti[5];
        *)
         val thetap1 = sub(ti, 0);
         val thetap2 = sub(ti, 1);
         val thetap3 = sub(ti, 2);
         val thetap4 = sub(ti, 3);
         val thetap5 = sub(ti, 4);

         (*
            for(i=1; i<=n; i++)
               ipeak[i] = 0.0;

            theta1 = Math.atan2(y[1],x[1]);
         *)
         val i = ref 0
         val _ = while (!i) < n do (
            update(ipeak, !i, 0.0);
            i := !i + 1
         )
         val theta1 = ref (Math.atan2(sub(y, 0), sub(x, 0)))
         val i = ref 0
         val theta2 = ref 0.0


         (*
         for(i=1; i<n; i++){
          theta2 = Math.atan2(y[i+1], x[i+1]);

          if( (theta1 <= thetap1) && (thetap1 <= theta2) ){
            d1 = thetap1 - theta1;
            d2 = theta2 - thetap1;
            if(d1 < d2)
                ipeak[i] = 1.0;
            else
                ipeak[i+1] = 1.0;
          }else if( (theta1 <= thetap2) && (thetap2 <= theta2) ){
            d1 = thetap2 - theta1;
            d2 = theta2 - thetap2;
            if(d1 < d2)
                ipeak[i] = 2.0;
            else
                ipeak[i+1] = 2.0;
          }else if( (theta1 <= thetap3) && (thetap3 <= theta2) ){
            d1 = thetap3 - theta1;
            d2 = theta2 - thetap3;
            if(d1 < d2)
                ipeak[i] = 3.0;
            else
                ipeak[i+1] = 3.0;
          }else if( (theta1 <= thetap4) && (thetap4 <= theta2) ){
            d1 = thetap4 - theta1;
            d2 = theta2 - thetap4;
            if(d1 < d2)
                ipeak[i] = 4.0;
            else
                ipeak[i+1] = 4.0;
          }else if( (theta1 <= thetap5) && (thetap5 <= theta2) ){
            d1 = thetap5 - theta1;
            d2 = theta2 - thetap5;
            if(d1 < d2)
                ipeak[i] = 5.0;
            else
                ipeak[i+1] = 5.0;
          }
          theta1 = theta2; 
        }
         *)

         val _ = while (!i) < (n-1) do (
            theta2 := Math.atan2(sub(y, (!i)+1), sub(x, (!i)+1));
            if !theta1 <= thetap1 andalso thetap1 <= !theta2 then (
               if thetap1 - !theta1 < !theta2 - thetap1 then 
                  update(ipeak, !i, 1.0)
               else
                  update(ipeak, (!i)+1, 1.0)
            ) else if !theta1 <= thetap2 andalso thetap2 <= !theta2 then (
               if thetap2 - !theta1 < !theta2 - thetap2 then 
                  update(ipeak, !i, 2.0)
               else
                  update(ipeak, (!i)+1, 2.0)
            ) else if !theta1 <= thetap3 andalso thetap3 <= !theta2 then (
               if thetap3 - !theta1 < !theta2 - thetap3 then
                  update(ipeak, !i, 3.0)
               else
                  update(ipeak, (!i)+1, 3.0)
            ) else if !theta1 <= thetap4 andalso thetap4 <= !theta2 then (
               if thetap4 - !theta1 < !theta2 - thetap4 then 
                  update(ipeak, !i, 4.0)
               else
                  update(ipeak, (!i)+1, 4.0)
            ) else if !theta1 <= thetap5 andalso thetap5 <= !theta2 then (
               if thetap5 - !theta1 < !theta2 - thetap5 then 
                  update(ipeak, !i, 5.0)
               else
                  update(ipeak, (!i)+1, 5.0)
            ) else ();
            theta1 := !theta2;
            i := !i + 1
         )

         (*

            /* correct the peaks */
            d = (int)Math.ceil(paramOb.getSfEcg()/64);
            for(i=1; i<=n; i++){ 
               if( ipeak[i]==1 || ipeak[i]==3 || ipeak[i]==5 ){

                  j1 = (1 > (i-d) ? 1 : (i-d)); //MAX(1,i-d);
                  j2 = (n < (i+d) ? n : (i+d)); //MIN(n,i+d);
                  jmax = j1;
                  zmax = z[j1];
                  for(j=j1+1;j<=j2;j++){ 
                     if(z[j] > zmax){
                           jmax = j;
                           zmax = z[j];
                     }
                  }
                  if(jmax != i){
                     ipeak[jmax] = ipeak[i];
                     ipeak[i] = 0;
                  }
               } else if( ipeak[i]==2 || ipeak[i]==4 ){
                  j1 = (1 > (i-d) ? 1 : (i-d));//MAX(1,i-d);
                  j2 = (n < (i+d) ? n : (i+d)); //MIN(n,i+d);
                  jmin = j1;
                  zmin = z[j1];
                  for(j=j1+1;j<=j2;j++){
                     if(z[j] < zmin){
                           jmin = j;
                           zmin = z[j];
                     }
                  }
                  if(jmin != i){
                     ipeak[jmin] = ipeak[i];
                     ipeak[i] = 0;
                  }
               }
            }
         *)
         val d = Real.ceil(sfecg_ / 64.0)
         val i = ref 0
         val j1 = ref 0
         val j2 = ref 0
         val jmax = ref 0
         val zmax = ref 0.0
         val jmin = ref 0
         val zmin = ref 0.0
         val j = ref 0
         val _ = while (!i) < n do (
            if Real.==(sub(ipeak, !i), 1.0) orelse Real.==(sub(ipeak, !i), 3.0) orelse Real.==(sub(ipeak, !i), 5.0) then (
               j1 := Int.max(1, !i-d);
               j2 := Int.min(n, !i+d);
               jmax := !j1;
               zmax := sub(z, !j1);
               j := !j1;
               while (!j) <= !j2 do (
                  if Real.>(sub(z, !j), !zmax) then (
                     jmax := !j;
                     zmax := sub(z, !j)
                  ) else ();
                  j := !j + 1
               );
               if !jmax <> !i then (
                  update(ipeak, !jmax, sub(ipeak, !i));
                  update(ipeak, !i, 0.0)
               ) else ()
            ) else if Real.==(sub(ipeak, !i), 2.0) orelse Real.==(sub(ipeak, !i), 4.0) then (
               j1 := Int.max(1, !i-d);
               j2 := Int.min(n, !i+d);
               jmin := !j1;
               zmin := sub(z, !j1);
               j := !j1;
               while (!j) < !j2 do (
                  if Real.<(sub(z, !j), !zmin) then (
                     jmin := !j;
                     zmin := sub(z, !j)
                  ) else ();
                  j := !j + 1
               );
               if !jmin <> !i then (
                  update(ipeak, !jmin, sub(ipeak, !i));
                  update(ipeak, !i, 0.0)
               ) else ()
            ) else ();
            i := !i + 1
         )
      in
         ()
      end

      (* 
        /* do peak detection using angle */
        ipeak = new double[Nts + 1];
        detectpeaks(ipeak, xts, yts, zts, Nts);
      *)
      val ipeak = array(Nts, 0.0)
      val _ = detectpeaks(ipeak, xts, yts, zts, Nts)
      (*
        /* scale signal to lie between -0.4 and 1.2 mV */
        zmin = zts[1];
        zmax = zts[1];
        for(i=2; i<=Nts; i++){
            if(zts[i] < zmin)
                zmin = zts[i];
            else if(zts[i] > zmax)
                    zmax = zts[i];
        }
        zrange = zmax-zmin;
        for(i=1; i<=Nts; i++)
            zts[i] = (zts[i]-zmin)*(1.6)/zrange - 0.4;
      *)

      val zmin = ref (sub(zts, 0))
      val zmax = ref (sub(zts, 0))
      val i = ref 1
      val _ = while (!i) <= Nts do (
         if Real.<(sub(zts, !i), !zmin) then
            zmin := sub(zts, !i)
         else if Real.>(sub(zts, !i), !zmax) then 
            zmax := sub(zts, !i)
         else ();
         i := !i + 1
      )
      val zrange = !zmax - !zmin
      val i = ref 0
      val _ = while (!i) <= Nts do (
         update(zts, !i, (sub(zts, !i)-(!zmin))*(1.6)/zrange - 0.4);
         i := !i + 1
      )

      (*
        /* include additive uniformly distributed measurement noise */
        for(i=1; i<=Nts; i++)
            zts[i] += paramOb.getANoise()*(2.0*ran1() - 1.0);

      *)

      val i = ref 0
      val _ = while (!i) <= Nts do (
         rtlock locknum;
         update(zts, !i, (sub(zts, !i) + (EcgParam.getANoise())*(2.0*(ran1()) - 1.0)));
         pos := !i;
         update(buffer, !i, sub(zts, !i));
         i := !i + 1;
         rtunlock locknum
      )

      (*
        /*
         * insert into the ECG data table
         */
        //ecgLog.println("Generating result matrix...");
        
        ecgResultNumRows = Nts;
        
        ecgResultTime = new double[ecgResultNumRows];
        ecgResultVoltage = new double[ecgResultNumRows];
        ecgResultPeak = new int[ecgResultNumRows];
        
        for(i=1;i<=Nts;i++){
            ecgResultTime[i-1] = (i-1)*tstep;
            ecgResultVoltage[i-1] = zts[i];
            ecgResultPeak[i-1] = (int)ipeak[i];
        }
      *)
      val ecgResultNumRows = Nts
      val ecgResultTime = array(ecgResultNumRows, 0.0)
      val ecgResultVoltage = array(ecgResultNumRows, 0.0)
      val ecgResultPeak = array(ecgResultNumRows, 0.0)
      val i = ref 0 

    (*

      val _ = while (!i) < Nts do ( 
         rtlock locknum;
         update(ecgResultTime, !i, (Real.fromInt(!i-1))*tstep);
         update(ecgResultVoltage, !i, sub(zts, !i));
         update(ecgResultPeak, !i, Real.fromInt(Real.trunc(sub(ipeak, !i))));
         if Real.isNan(sub(ecgResultVoltage, !i)) then () else (
         printr (sub(ecgResultTime, !i)); print "\t";
         printr (sub(ecgResultVoltage, !i)); print "\t";
         printr (sub(ecgResultPeak, !i)); print "\n");
         pos := !i;
         update(buffer, !i, sub(zts, !i));
         i := !i + 1;
         rtunlock locknum
      )
    *)

      fun printvals () =
      let
         val i = ref 0
      in
         while (!i) < Nts do (
            if Real.isNan(sub(ecgResultVoltage, !i)) then () else (
               printr (sub(ecgResultTime, !i)); print "\t";
               printr (sub(ecgResultVoltage, !i)); print "\t";
               printr (sub(ecgResultPeak, !i)); print "\n"
            );
            i := !i + 1
         ); !i
      end

   in (*
      fun dorun () = 
      let
      in 
         print ("Q: "^(Int.toString q)^"\n");*)
         print (Int.toString(getMyPriority ())^"] ECG frame generated. "^Int.toString(ecgResultNumRows)^"\n")
         (*;print (Int.toString(printvals ()))
      end *)
   end

end
