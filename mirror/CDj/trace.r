
#
# These R functions were used to produce graphs in the CDx paper.
# Some of them are generic, while some were hacked by hard-coded parameters
# to make the graphs look pretty in the paper and with the specific 
# workloads.
#
# TODO: these functions should be cleaned-up, made general, and documented.
#

# 0 0 900.0 100.0 5.0
# [frame_no] [plane_no] [x] [y] [z]

readTrace = function(workload) {
  m = as.matrix(read.table(paste("trace_",workload,".rin",sep="")))
  colnames(m) = c("frame","plane","x","y","z")
  m
}

getFPS = function(workload) {
  if (workload=="easy") {
    return (250)
  }
  if (workload=="hard") {
    return (100)
  }
  stop("Unknown wokload")
}

# eucledian distance of vectors a and b
dist = function( a, b ) {

  n = length(a)
  if (n!=length(b)) {
    stop("Vector lengths differ")
  }

  sqrt( sum ( sapply(1:n, function(i) {
    (a[i] - b[i])^2
  })))

}

speed = function( plane, workload, trace=readTrace(workload=workload), 
	fps=getFPS(workload) ) {

  positions = trace[ trace[,"plane"]==plane, c("x","y","z") ]

  n = nrow(positions)
  if (n<2) {
    stop("Need at least two frames.")
  }

  distances = sapply( 2:n, function(i) {
    dist( a=positions[ i, ], b=positions[ i-1, ] )
  }
  )

  distances * fps
}

allspeeds = function( planes=unique(sort(trace[,2])), 
	workload, 
	trace=readTrace(workload=workload), ... ) {

  do.call( cbind, lapply( planes, function (p) { 
	speed( plane = p, trace = trace, workload=workload, ... )
  }
  ))
}

ycoords = function( 
	workload,
	planes=unique(sort(trace[,2])), 
	trace=readTrace(workload=workload), ... ) {

  sapply( planes, function(p) {
	  min( trace[ trace[,"plane"]==p, "y"] )
  }
  )
}

ply = function( trace_easy, trace_hard ) {
  yc_easy=ycoords(workload="easy", trace=trace_easy)

  png(filename="y_easy.png", width=500, height=400)
  plot(yc_easy, pch=20, xlab="Aircraft Id", ylab="Y of Aircraft",
	cex.main=1.4, cex.lab=1.4,
	cex.sub=1.4, cex.axis=1.4, axes=FALSE, frame.plot=TRUE,
  )

  axis(1, at=c(1,10), cex.main=1.4, cex.lab=1.4, cex.sub=1.4, cex.axis=1.4)
  axis(3, at=c(11,20), cex.main=1.4, cex.lab=1.4, cex.sub=1.4, cex.axis=1.4)
  axis(2, c(100,120,130,150), cex.main=1.4, cex.lab=1.4, cex.sub=1.4, cex.axis=1.4)
  dev.off()

  png(filename="y_hard.png", width=500, height=400)
  yc_hard=ycoords(workload="hard", trace=trace_hard)

  plot(yc_hard, pch=20, xlab="Aircraft Id", ylab="Y of Aircraft", 
	cex.main=1.4, cex.lab=1.4,
  	cex.sub=1.4, cex.axis=1.4, axes=FALSE, frame.plot=TRUE,
  )
  axis(1, c(1,10,21,30), cex.main=1.4, cex.lab=1.4, cex.sub=1.4, cex.axis=1.4)
  axis(3, c(11,20,31,40), cex.main=1.4, cex.lab=1.4, cex.sub=1.4, cex.axis=1.4)
  axis(2, c(100,120,130,150), cex.main=1.4, cex.lab=1.4, cex.sub=1.4, cex.axis=1.4)
  dev.off()

}

plx = function( trace_easy, trace_hard ) {

  png(filename="x_easy.png", width=500, height=400)
  plot( trace_easy[,"frame"]/getFPS(workload="easy"), trace_easy[,"x"], pch=".", xlab="Time [s]", 
        ylab="X of Aircraft", cex.main=1.4, cex.lab=1.4, cex.sub=1.4, 
        cex.axis=1.4, xlim=c(0,31)) 
  dev.off()

  png(filename="x_hard.png", width=500, height=400)
  plot( trace_hard[,"frame"]/getFPS(workload="hard"), trace_hard[,"x"], pch=".", xlab="Time [s]", 
        ylab="X of Aircraft", cex.main=1.4, cex.lab=1.4, cex.sub=1.4, 
        cex.axis=1.4, xlim=c(0,31)) 
  dev.off()
}

plv = function( trace_easy, trace_hard ) {

  v_easy = speed( plane=0, workload="easy", trace=trace_easy)


  png(filename="v_easy.png", width=500, heigh=400)
  plot( (1:length(v_easy))/getFPS(workload="easy"), v_easy, type="l", xlab="Time [s]", ylab="Aircraft Speed",
    xlim=c(0,30), cex.main=1.4, cex.lab=1.4, cex.sub=1.4, cex.axis=1.4, frame.plot=TRUE,
    axes=FALSE, lwd=2)
  axis(1, cex.main=1.4, cex.lab=1.4, cex.sub=1.4, cex.axis=1.4)
  axis(2, c(0,100,200), cex.main=1.4, cex.lab=1.4, cex.sub=1.4, cex.axis=1.4)
  dev.off()

  v_hard_0 = speed( plane=0, workload="hard", trace=trace_hard)
  v_hard_1 = speed( plane=69, workload="hard", trace=trace_hard)

  png(filename="v_hard.png", width=500, heigh=400)
  plot( (1:length(v_hard_0))/getFPS(workload="hard"), v_hard_0, type="l", xlab="Time [s]", ylab="Aircraft Speed", 
    xlim=c(0,30), cex.main=1.4, cex.lab=1.4, cex.sub=1.4, cex.axis=1.4, frame.plot=TRUE, 
    axes=FALSE, lwd=2)
  lines(  (1:length(v_hard_1))/getFPS(workload="hard"), v_hard_1, type="l", lwd=2)
  axis(1, cex.main=1.4, cex.lab=1.4, cex.sub=1.4, cex.axis=1.4)
  axis(2, c(0,100,200), cex.main=1.4, cex.lab=1.4, cex.sub=1.4, cex.axis=1.4)
  dev.off()
}

pla_int = function(workload,warmup=20,...) {
 d=as.matrix(read.table(paste("results/",workload,"/5/detector.rin",sep="")))
  
  times = (d[,1]-d[1,1])/1e9
  cmems = (d[,3]-d[,4])/(1024)

  if (length(cmems[cmems<0])!=0) {
    stop("There was GC during detector")
  }

  xlim=c(warmup,max(times))
  ylim=range(cmems[times>warmup])

  plot( times, cmems, xlab="Time [s]", ylab="Detector Allocation [KB]", 
	pch=19, cex=0.3, cex.main=1.4, cex.lab=1.4, cex.sub=1.4, cex.axis=1.4, 
	xlim=xlim,ylim=ylim,...)
}

pla = function(workload) {

  png(filename="atrace_easy.png", width=600, heigh=450)
  pla_int(workload="easy")
  dev.off()

  png(filename="atrace_hard.png", width=600, heigh=450)
  pla_int(workload="hard")
  dev.off()
}

plt_int = function(workload,warmup=20,...) {
 d=as.matrix(read.table(paste("results/",workload,"/5/detector.rin",sep="")))
  
  times = (d[,1]-d[1,1])/1e9
  ctimes = (d[,2]-d[,1])/(1e6)

  xlim=c(warmup,max(times))
  ylim=range( ctimes[ times>warmup ] )
  plot( times, ctimes, xlab="Time [s]", ylab="Computation Time [ms]", 
	pch=19, cex=0.3, cex.main=1.4, cex.lab=1.4, cex.sub=1.4, cex.axis=1.4, 
	xlim=xlim, ylim=ylim, ...)
}

plt = function(workload) {

  png(filename="ttrace_easy.png", width=600, heigh=450)
  plt_int(workload="easy")
  dev.off()

  png(filename="ttrace_hard.png", width=600, heigh=450)
  plt_int(workload="hard")
  dev.off()
}



ple = function() {

 png(filename="ye.png", width=500, height=400)
 plot(yc, pch=20, xlab="Aircraft Index", ylab="Y of Aircraft", cex.main=1.4, cex.lab=1.4,
	cex.sub=1.4, cex.axis=1.4, axes=FALSE, frame.plot=TRUE,
	ylim=c(110,140),xlim=c(0,20.5))
 axis(1, at=c(1,5,10,15,20), cex.main=1.4, cex.lab=1.4, cex.sub=1.4,
    cex.axis=1.4)
 axis(2, c(100,120,130,150), cex.main=1.4, cex.lab=1.4,
 cex.sub=1.4, cex.axis=1.4)> dev.off()
 dev.off() 

 png(filename="ye.png", width=500, heigh=400)
 plot(yc, pch=20, xlab="Aircraft Index", ylab="Y of Aircraft", cex.main=1.4, cex.lab=1.4,
 	cex.sub=1.4, cex.axis=1.4)
 dev.off()

 png(filename="xzoome.png", width=500, heigh=400)
 plot( traces[,"frame"]/100, traces[,"x"], pch=".", xlab="Times [s]", 
	ylab="X of Aircraft", xlim=c(0,30), ylim=c(400,1000), cex.main=1.4, cex.lab=1.4,
	cex.sub=1.4, cex.axis=1.4)
 dev.off()

 png(filename="xe.png", width=500, heigh=400)
 plot( traces[,"frame"]/100, traces[,"x"], pch=".", xlab="Times [s]", 
	ylab="X of Aircraft", cex.main=1.4, cex.lab=1.4, cex.sub=1.4, 
	cex.axis=1.4) 
 dev.off()
}

pl = function() {

 traces=readTrace()
# png(filename="xzoom.png", width=600, heigh=450)
# plot( traces[,"frame"]/100, traces[,"x"], pch=".", xlab="Times [s]", ylab="X Coordinate of Aircraft", xlim=c(0,30), ylim=c(400,1000))
 png(filename="xzoom.png", width=500, heigh=400)
 plot( traces[,"frame"]/100, traces[,"x"], pch=".", xlab="Times [s]", ylab="X of Aircraft", xlim=c(0,30), ylim=c(400,1000), cex.main=1.4, cex.lab=1.4, cex.sub=1.4, cex.axis=1.4)
 dev.off()


# png(filename="x.png", width=600, heigh=450)
# plot( traces[,"frame"]/100, traces[,"x"], pch=".", xlab="Times [s]", ylab="X Coordinate of Aircraft")
 png(filename="x.png", width=500, heigh=400)
 plot( traces[,"frame"]/100, traces[,"x"], pch=".", xlab="Times [s]", ylab="X of Aircraft", cex.main=1.4, cex.lab=1.4, cex.sub=1.4, cex.axis=1.4)
 dev.off()

 yc =ycoords()

 png(filename="y.png", width=500, heigh=400)  
 plot(yc, pch=20, xlab="Aircraft Index", ylab="Y of Aircraft", cex.main=1.4, cex.lab=1.4, cex.sub=1.4, cex.axis=1.4)

 s=speed(0)

 png(filename="v.png", width=600, heigh=450)
 plot( (1:length(s))/100, s, type="l", xlab="Times [s]", ylab="Instantaneous Aircraft Speed")
 dev.off()

 png(filename="col.png", width=600, height=450)
 collisions()
 dev.off()

 ct=ctime()
 png(file="ctimecols.png", width=600, height=450)
 plot(ct[,1], ct[,3], pch=20, type="o", xlab="Number of Collisions", ylab="Detector Computation Time [ms]", ylim=range(ct[,2], ct[,3]))
 lines(ct[,1], ct[,2], pch=20, type="o", lty="dashed")
 legend(x="top", legend=c("Maximum", "Mean"), pch=20, lty=c("solid","dashed"))
 dev.off()

 cm=cmem()
 png(file="cmemcols.png", width=600, height=450)
 plot(cm[,1], cm[,3], pch=20, type="o", xlab="Number of Collisions", ylab="Detector Allocation [MB]", ylim=range(cm[,2], cm[,3]))
 lines(cm[,1], cm[,2], pch=20, type="o", lty="dashed")
 legend(x="top", legend=c("Maximum", "Mean"), pch=20, lty=c("solid","dashed"))
 dev.off()

}

plc = function() {
  png(filename="col.png", width=600, height=450)
  collisions()
  dev.off()
}

plct = function() {

 ct=ctime()
# png(file="ctimecols.png", width=600, height=450)
 plot(ct[,1], ct[,3], pch=20, type="o", xlab="Number of Collisions", ylab="Detector Computation Time [ms]", ylim=range(ct[,2], ct[,3]))
 lines(ct[,1], ct[,2], pch=20, type="o", lty="dashed")
 legend(x="top", legend=c("Maximum", "Mean"), pch=20, lty=c("solid","dashed"))
# dev.off()

}

collisions = function(fname = "results/hard/5/detector.rin") {

  d=as.matrix(read.table(fname))
  
  times = (d[,1]-d[1,1])/1e9
  scols = d[,5]
  dcols = d[,6]

  plot( times[scols>0], max(scols) - scols[scols>0]  , col="black", 
	ylim=c(0,max(scols)+max(dcols)+0), axes = FALSE,
  	xlab="Times [s]",pch=19,cex=0.8, ylab="", 
	frame.plot=TRUE,
	xlim=c(-1,101), 
	cex.main=1.4, cex.lab=1.4, cex.sub=1.4,
        cex.axis=1.4)

  points( times[dcols>0], dcols[dcols>0] + max(scols), col="black", pch=19, cex=0.8)
  abline( h=max(scols))

  axis(1,cex.main=1.4, cex.lab=1.4,  cex.sub=1.4, cex.axis=1.4)
#  axis(2)
  axis(2, at=max(scols)+c(-20,-10, 0,20,40,60,80), 
	labels=c("20","10", "0","20","40","60","80"),
	cex.main=1.4, cex.lab=1.4,
	 cex.sub=1.4, cex.axis=1.4)
#  legend(x=3,"detected collisions", y=60, box.lty=0)
#  legend(x=3,"grid elements\nwith multiple aircraft", y=40, box.lty=0)
}

ctimes = function(fname = "detector.rin") {

 d=as.matrix(read.table("detector.rin"))
  
  times = (d[2001:9999,1]-d[2001,1])/1e9
  ctimes = (d[2001:9999,2]-d[2001:9999,1])/1e6

  plot( times, ctimes, xlab="Time [s]", ylab="Detector Computation Time [ms]", 
	pch=19, cex=0.8)
 
}

ctimesnw = function(fname = "detector.rin") {

 d=as.matrix(read.table("detector.rin"))
  
  times = (d[1:9999,1]-d[1,1])/1e9
  ctimes = (d[1:9999,2]-d[1:9999,1])/1e6

  plot( times, ctimes, xlab="Time [s]", ylab="Detector Computation Time [ms]", 
	pch=19, cex=0.3)
 
}

cmemsnw = function(fname = "detector.rin") {

 d=as.matrix(read.table("detector.rin"))
  
  times = (d[1:9999,1]-d[1,1])/1e9
  cmems = (d[1:9999,3]-d[1:9999,4])/(1024*1024)

  plot( times, cmems, xlab="Time [s]", ylab="Detector Allocation [MB]", 
	pch=19, cex=0.3)
}

arate = function(fname = "detector.rin") {

 d=as.matrix(read.table(fname))
  
 times = (d[1:9999,1]-d[1,1])/1e9
 cmems = (d[1:9999,3]-d[1:9999,4])/(1024*1024)
 ctimes = (d[1:9999,2]-d[1:9999,1])/1e6

 plot( times, cmems / (ctimes/1e3), ylab="Detector Allocation Rate [MB/s]",
	xlab="Time [s]",
	pch=19, cex=0.3)
}

loadcmem = function(fname = "detector.rin", warmup=2000) {

  d=as.matrix(read.table(fname))
  dcols = d[(warmup+1):9999,6]
  cmem = (d[(warmup+1):9999,3]-d[(warmup +1):9999,4])/(1024*1024)

  if ( length( cmem[cmem<0] ) > 0 ) {
    browser()
  }
  m=cbind(dcols, cmem)
  colnames(m) = c("collisions", "cmem")
  m
}

loadctime = function(fname = "detector.rin", warmup=2000) {

  d=as.matrix(read.table(fname))
  dcols = d[(warmup+1):9999,6]
  ctime = (d[(warmup+1):9999,2]-d[(warmup +1):9999,1])/1e6

  if ( length( ctime[ctime<0] ) > 0 ) {
    browser()
  }
  m=cbind(dcols, ctime)
  colnames(m) = c("collisions", "ctime")
  m
}

cmem = function( dirs=1:50 ) {

  alldata = do.call( rbind, lapply( dirs, function(dir) {

        fn = paste("current/",dir,"/detector.rin",sep="")
        loadcmem(fname = fn )
  }
  ))

  colnums = sort(unique(alldata[,1]))
  means = sapply( colnums, function(cols) {
    mean(alldata[ alldata[,1] == cols, 2 ])
  })
  maxs = sapply( colnums, function(cols) {
    max(alldata[ alldata[,1] == cols, 2 ])
  })
  m=cbind(colnums, means,maxs)
  colnames(m) = c("collisions", "meancmem", "maxcmem")
  m
}

ctime = function( dirs=1:50 ) {

  alldata = do.call( rbind, lapply( dirs, function(dir) {

        fn = paste("results/hard/",dir,"/detector.rin",sep="")
        loadctime(fname = fn )
  }
  ))

  browser()
  colnums = sort(unique(alldata[,1]))
  means = sapply( colnums, function(cols) {
    sel =  alldata[,"collisions"] == cols
    mean(alldata[ sel, "ctime"])
  })
  maxs = sapply( colnums, function(cols) {
    sel =  alldata[,"collisions"] == cols
    mean(alldata[ sel, "ctime"])
  })
  m=cbind(colnums, means,maxs)
  colnames(m) = c("collisions", "meanctime", "maxctime")
  m
}

# ----------------------

expandNames = function(x) {x}

responseTimeRSPlot = function(workload,warmup=20,...) {
  
  rtimes = loadDetectorResponseTimeStats( workloads = workload,
	cacheName="identityOneRun",
	warmup=warmup,
	stat=function(x) {x},
	nruns=1 )

  periodTimeSec=1/getFPS(workload = workload)
  #warmupPeriods=ceiling(warmup*1e9/periodTime)

  times = (warmup+(1:length(rtimes))*periodTimeSec)
  plot( times, rtimes, xlab="Time [s]", ylab="Response Time [ms]", 
	pch=19, cex=0.3, cex.main=1.4, cex.lab=1.4, cex.sub=1.4, cex.axis=1.4, 
	...)
}


responseTimes0 = function( workloads,... ) {

  maxs=loadDetectorResponseTimeStats( workloads=workloads, stat = max, cacheName="max")
  mins=loadDetectorResponseTimeStats( workloads=workloads, stat = min, cacheName="min")

  mhistPlot(
    st=loadDetectorResponseTimeStats( workloads=workloads, stat = function(x) { x }, cacheName="identity"), 
    maxs=maxs,
    mins=mins,
    main="",
    ylab="Response Time [ms]",
    cnames="",
    lf=TRUE,
    ...
  )
}

responseTimes = function( workload, warmup=20, ... ) {

  rtimes = loadDetectorResponseTimeStats( workloads = workload,
	cacheName="identityOneRun",
	warmup=warmup,
	stat=function(x) {x},
	nruns=1 )

  periodTimeSec=1/getFPS(workload = workload)

  times = (warmup+(1:length(rtimes))*periodTimeSec)

  #xat = round( c(min(times), max(times), min(times) + (max(times)-min(times))/2), digits=0)
  xat = c(20,40,60,80,100)


  maxs=loadDetectorResponseTimeStats( workloads=workload, stat = max, cacheName="max_old")
  mins=loadDetectorResponseTimeStats( workloads=workload, stat = min, cacheName="min_old")

  mhistPlot(
    st=loadDetectorResponseTimeStats( workloads=workload, stat = function(x) { x }, cacheName="identity_old"), 
    rsplots=TRUE,
    rstimes = times,
    rsvalues = rtimes,
    maxs=maxs,
    mins=mins,
    main="",
    ylab="Response Time [ms]",
    cnames="",
    lf=TRUE,
    rsxat=xat,
    ...
  )
}

# WARNING !!! if used for multiple workloads, it cuts the number of values
#   to the smaller amount available

loadDetectorResponseTimeStats = function( 
	workloads,
	warmup = 20, # in seconds,
	periodTimes = sapply(workloads, function(workload) {
		1e9/getFPS(workload = workload)
	}), 
	stat=max,
	nruns=50,
	cacheName="defaultCache") {

  r=sapply( 1:length(workloads) , function(wIndex) {

    workload=workloads[wIndex]
    periodTime=periodTimes[wIndex]
    warmupPeriods=ceiling(warmup*1e9/periodTime)

    cacheFileName = paste( "results/", workload, "_", cacheName, ".xdr", sep="")
    if (file.exists(file = cacheFileName)) {
      load(cacheFileName)
      completionStats
    } else {

    v=sapply( 1:nruns, function(r) {
     
      # actual_release_time ideal_release_time reported_miss(0 or 1)
      fn = paste( "results/",workload,"/",r,"/release.rin",sep="" )
      d=as.matrix(read.table(fn)) 

      # start_time end_time start_free end_free suspected_collisions detected_collisions
      mfn = paste( "results/",workload,"/",r,"/detector.rin",sep="" )
      m=as.matrix(read.table(mfn))


      # abort if any reported misses after warmup
      reportedMissed = sum(d[(warmupPeriods+1):nrow(d),3])

      if (reportedMissed!=0) {
        stop("There are reported non-warmup deadline misses")
      }

      # non-(reported)-miss data
      nmd = d[ d[,3]==0, ]

      # index of the first non-warm-up release in ideal values (nmd[,2])
      idealStart = warmupPeriods+1

      # when the first non-warm-up-release should ideally happen
      firstPeriod = nmd[idealStart,2]

      # distance from the ideal first release to the closest actual release 
      distance = min(abs(nmd[,1] - firstPeriod))

      # index of the closest actual release to the ideal release, in real values(m[,2])
      # assume this is the actual first release 
      realStart = min(  (1:nrow(nmd)) [ abs(nmd[,1] - firstPeriod) == distance ]  )

      realValues = nrow(nmd) - realStart + 1
      idealValues = nrow(nmd) - idealStart  + 1
      commonValues = min(realValues, idealValues)

      ct = (m[realStart+(0:commonValues-1),2]-nmd[idealStart+(0:commonValues-1),2])/1e6

      mid = ct > periodTime
      nmid = length( mid[ mid ] )
      if (nmid > 0) {
        stop("There are missed deadlines (unreported) in non-warmup.")
      }
      stat(ct)	
    }
    )   

   # if the numbers of values in different runs are not equal, we get a list
   completionStats = unlist(v)
   save(completionStats, file=cacheFileName)
   completionStats
   }
  }
  )

  # r may not be a matrix, because the number of values in rows may differ

  if (is.list(r)) {
	ncols = length(r)
	minRows = min( sapply(1:ncols, function(i) { length(r[[i]]) }) )
	rm = do.call(cbind, lapply( 1:ncols, function(i) { 
		(r[[i]])[1:minRows] 
	}	
	))
	colnames(rm)=names(r)
	r = rm
  }
  r
}


rtSum = function(data) {

  v = c(
	min(data),
	mean(data),
	sd(data),
	max(data))

   m = matrix(byrow=TRUE, nrow=1, v)
   colnames(m) = c("min","avg","stddev","max")
   m

}

# overall statistics from all data of all runs

printDetectorStats = function( workload, 
	metrics=c("responseTime", "computationTime", "jitter"),
	sumFunc = summary) {

  r = loadDetectorStats(workloads=workload, stat=function(x) { x }, cacheName="identity")

  # only one workload
  r = r[[1]]

  res = do.call(rbind, lapply( metrics , function(metric) {

    data = round( unlist(r[[metric]]), digits=3)
    round(sumFunc(data), digits=3)
  }
  )
  )

  rownames(res) = metrics
  res

}

# release jitter, computation time, response time

loadDetectorStats = function( 
	workloads,
	warmup = 20, # in seconds,
	periodTimes = sapply(workloads, function(workload) {
		1e9/getFPS(workload = workload)
	}), 
	stat=max,
	nruns=50,
	cacheName="defaultCombinedCache") {

  r=lapply( 1:length(workloads) , function(wIndex) {

    workload=workloads[wIndex]
    periodTime=periodTimes[wIndex]
    warmupPeriods=ceiling(warmup*1e9/periodTime)

    cacheFileName = paste( "results/", workload, "_", cacheName, ".xdr", sep="")

    if (file.exists(file = cacheFileName)) {
      load(cacheFileName)
    } else {

      v=lapply( 1:nruns, function(r) {
      
        # actual_release_time ideal_release_time reported_miss(0 or 1)
        fn = paste( "results/",workload,"/",r,"/release.rin",sep="" )
        d=as.matrix(read.table(fn)) 

        # start_time end_time start_free end_free suspected_collisions detected_collisions
        mfn = paste( "results/",workload,"/",r,"/detector.rin",sep="" )
        m=as.matrix(read.table(mfn))


        # abort if any reported misses after warmup
        reportedMissed = sum(d[(warmupPeriods+1):nrow(d),3])

        if (reportedMissed!=0) {
          stop("There are reported non-warmup deadline misses")
        }

        # non-(reported)-miss data
        nmd = d[ d[,3]==0, ]

        # index of the first non-warm-up release in ideal values (nmd[,2])
        idealStart = warmupPeriods+1

        # when the first non-warm-up-release should ideally happen
        firstPeriod = nmd[idealStart,2]

        # distance from the ideal first release to the closest actual release 
        distance = min(abs(nmd[,1] - firstPeriod))

        # index of the closest actual release to the ideal release, in real values(m[,2])
        # assume this is the actual first release 
        realStart = min(  (1:nrow(nmd)) [ abs(nmd[,1] - firstPeriod) == distance ]  )

        realValues = nrow(nmd) - realStart + 1
        idealValues = nrow(nmd) - idealStart  + 1
        commonValues = min(realValues, idealValues)

        # response time
        responseTime = (m[realStart+(0:commonValues-1),2]-nmd[idealStart+(0:commonValues-1),2])/1e6

        mid = responseTime > periodTime
        nmid = length( mid[ mid ] )
        if (nmid > 0) {
          stop("There are missed deadlines (unreported) in non-warmup.")
        }

        # jitter
        jitter = (nmd[realStart+(0:commonValues-1),1]-nmd[idealStart+(0:commonValues-1),2])/1e6    
    
        # computation time
        computationTime = (m[realStart+(0:commonValues-1),2]-nmd[realStart+(0:commonValues-1),1])/1e6

        result = list()
        result[["responseTime"]] = stat(responseTime)
        result[["jitter"]] = stat(jitter)
        result[["computationTime"]] = stat(computationTime)

        result	
      }
      )   

      # v is a list of length nruns, indexed 1:nruns, each element is a list
      #   with elements responseTime, jitter, computationTime
  
      # append values from all runs 

      responseTime = sapply( 1:nruns, function(runIndex) { (v[[runIndex]])$responseTime })
      jitter = sapply( 1:nruns, function(runIndex) { (v[[runIndex]])$jitter })
      computationTime = sapply( 1:nruns, function(runIndex) { (v[[runIndex]])$computationTime })

      save(responseTime, jitter, computationTime, file=cacheFileName)
    }
   
    result = list()
    result[["responseTime"]] = responseTime
    result[["jitter"]] = jitter
    result[["computationTime"]] = computationTime
    result[["workloadName"]] = workload
    result[["warmupInSeconds"]] = warmup
 
    result
  }
  )

  # returns a list of elements 1 .. no_of_workloads
  #  each element contains a list with elements
  #    responseTime
  #    computationTime
  #    jitter
  #    workloadName

  #  the xTime is a list of 1 .. nruns
  #    each element is a list of values returned by the statistics

  r
}

mhistPlot = function(st, 
        cnames = colnames(st),
	mins=c(), 
	maxs=c(), 
        oneExtremeOnly=TRUE,
	hscale=80,
	hspace=10,
        rsscale=140,
        rsspace=20,
	xlab="", 
	ylab="",
	main="", 
	maxValue=NA, 
	boxplots=TRUE, 
        rsplots=TRUE,
	rsxat=c(),
        bspace = if (boxplots) { 30 } else { 0 }, 
	plotxlim = c(),
	plotylim = c(),
	rstimes = c(),
	rsvalues = c(),
        extremesShift = 0,
	lf = FALSE,
	...) {

  if (is.na(maxValue)) {
    myfilter = function(d) { d }
  } else {
    myfilter = function(d) { d[d<=maxValue] }
  }

  mydata = myfilter(as.numeric(st))

  oldpar = par(no.readonly = TRUE)
  par( mar = c(5, 5.5, 4, 3.5) + 0.1 )
  par( xaxs="i", yaxs="i" )

  h=hist(mydata,plot=FALSE,...)
  brk=h$breaks
  mids=h$mids

  if (length(plotylim)==0) {
    plotylim=c( floor(min(mydata,mids,brk) - abs(min(mydata,mids,brk))*.02), 
	ceiling(max(mydata,mids,brk) + abs(max(mydata,mids,brk))*.05 ))
  }

  if (length(plotxlim)==0) {
    plotxlim=c(0,ncol(st)*(hscale+hspace+bspace+rsscale+rsspace))
  }

  cnt=apply(st, 2, function(d) { hist(myfilter(d),plot=FALSE,breaks=brk)$counts })

#  par(new=FALSE)

   mpf = function(...) {
	  matplot(0,0,xlim=plotxlim, ylim=plotylim, main=main,
		xlab=xlab, ylab=ylab,axes=FALSE,xaxs="i",yaxs="i",frame.plot=TRUE,...)
   }

   if (lf) {
	mpf(cex.axis=1.0,cex.lab=1.0,cex.main=1.0)
   } else {
	mpf()
   }
 
  
  
  if (lf) {
    axis(2, cex.axis=1.0,cex.lab=1.0,cex.main=1.0, cex.sub=1.0)
  } else {
    axis(2)
  }
#  grid(nx=0, ny=NULL)

  af = function(...) {
	  axis(1, at=((0:(ncol(st)-1))*(hscale+hspace+bspace+rsscale+rsspace))+(hscale+hspace+bspace+rsscale+rsspace)/2 + hspace, 
		labels=cnames,
		tick=FALSE, ...)
  } 
	
  if (lf) {
	af(cex.lab=1.0,cex.sub=1.0,cex=1.0,cex.axis=1.0)
  } else {
	af()
  }
	
  for(i in 1:ncol(st)) {

	  if (boxplots) {
		at=hspace + i*(hscale+hspace+bspace+rsscale+rsspace)-(hspace+rsscale+rsspace+bspace*.25)

#		lines(  c(at,at), c(max(st[,i]),  min(st[,i])), lwd=1, col="grey")
		boxplot(myfilter(st[,i]),
			xlim=plotxlim,
			ylim=plotylim,
			axes=FALSE,
			boxwex=hscale/2, pch=".", 
			at=at,
			offset=0, xaxs="i",yaxs="i",
			add=TRUE,
#	                outline=TRUE,
	                outline=FALSE,
			pch=19)

    

	        points(at , max(st[,i]), pch=19, col="red")
	        points(at , min(st[,i]), pch=19, col="green")

		if (!oneExtremeOnly) {
		if (length(maxs)>0) {
			points( rep(at + extremesShift, nrow(maxs)), maxs[,i], pch=18, col="red")
		}

		if (length(mins)>0) {
			points( rep(at - extremesShift, nrow(mins)), mins[,i], pch=18, col="green")
		}
 		}
	  }

  }

  for(i in 1:ncol(st)) {

	  if (TRUE) {
   	    par(new=TRUE)
	    xlimbp = plotxlim-(i-1)*(hscale+hspace+bspace+rsscale+rsspace) - hspace
            ylimbp = plotylim - min(mids)
	    barplot( 
		(cnt[,i]/max(cnt))*hscale, 
		horiz=TRUE,
		axes=FALSE,
		space=0,
		width=mids[2]-mids[1], 
		xlim=xlimbp,
		ylim=ylimbp,
		offset=0, xaxs="i", yaxs="i", border=TRUE)
	  }

  }

  for(i in 1:ncol(st)) {

	#par(new=TRUE)
	#plot((1:100)/100,xlim=c(-100,100), ylim=plotylim, axes=TRUE)
  	if (TRUE) {
		
		startAt = (i-1)*(hscale+hspace+bspace+rsscale+rsspace) + 
			(hscale + bspace)*.9
                endAt = startAt + rsscale

		xlimrsp = plotxlim-startAt
            	ylimrsp = plotylim

		scaleit = function(xs) {
			sapply(xs, function(x) { x * rsscale / (max(rstimes)-min(rstimes)) } )
		}

		par(new=TRUE)
		plot( scaleit(rstimes), 
			rsvalues, xlab="", ylim=ylimrsp,
			xlim=xlimrsp,
			pch=19, cex=0.3, cex.main=1.0, cex.lab=1.0, cex.sub=1.0, cex.axis=1.0,
			axes=FALSE,
			xaxs="i", yaxs="i", ylab="")
		
		axis(1, at=scaleit(rsxat),
			rsxat, 
			cex.axis=1.0,cex.lab=1.0,cex.main=1.0, cex.sub=1.0,
			xaxs="i", yaxs="i"
			)
		#text( x=scaleit( (max(rstimes)-min(rstimes))/2 ), y=0, labels="Time [s]" )
	}


  }


  par(oldpar)
}

