signature EcgParam = 
sig
    fun getSf : () -> int
    fun getSfEcg : () -> int

    fun getN : () -> int
    fun getHrStd : () -> real
    fun getHrMean : () -> real
    fun getLfHfRatio : () -> real
    fun getAmplitude : () -> real
    fun getSeed : () -> int
    fun getANoise : () -> real
    fun getFLo : () -> real
    fun getFHi : () -> real
    fun getFLoStd : () -> real
    fun getFHiStd : () -> real
    fun getTheta : () -> real array
    fun getA : () -> real array
    fun getB : () -> real array

end