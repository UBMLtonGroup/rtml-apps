signature EcgCalc = 
sig
    fun ran1 : () -> real

    fun fft px py np : real array -> real array -> int -> ()
    fun ifft datar datai nn : real array -> real array -> int -> ()

    fun stdevl x n : real list -> int -> real
    fun stdeva x n : real array -> int -> real

end