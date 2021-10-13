structure EcgParam = 
struct
    fun getSf () = 512         (* Internal sampling frequency            *)
    fun getSfEcg () = 256      (* ECG sampling frequency                 *)

    fun getN () = 256          (* Number of heart beats                  *)
    fun getHrStd () = 1.0      (* Heart rate std                         *)
    fun getHrMean () = 60.0    (* Heart rate mean                        *)
    fun getLfHfRatio () = 0.5  (* LF/HF ratio                            *)
    fun getAmplitude () = 1.4  (* Amplitude for the plot area - not used *)
    fun getSeed () = 1         (* Seed                                   *)
    fun getANoise () = 0.1     (* Amplitude of additive uniform noise    *)
    fun getFLo () = 0.1        (* Low freq                               *)
    fun getFHi () = 0.25       (* Hi freq                                *)
    fun getFLoStd () = 0.01    (* Low freq std                           *)
    fun getFHiStd () = 0.01    (* Hi freq std                            *)

    (* Orders of extrema: P Q R S T *)

    fun getTheta () = Array.fromList([~60.0, ~15.0, 0.0, 15.0, 90.0])
    fun getA () = Array.fromList([1.2, ~5.0, 30.0, ~7.5, 0.75])
    fun getB () = Array.fromList([0.25, 0.1, 0.1, 0.1, 0.4])

end