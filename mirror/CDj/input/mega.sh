#! /bin/bash

N=0
MAX=10

while [ $N -lt $MAX ] ; do

  cat << EOF
add	lineara$N	8-($N/$MAX)-t	000+t+($N/$MAX)	5	ord	ind
add	linearb$N	000+t+($N/$MAX) 	8-($N/$MAX)-t	5	ord	ind
EOF
  N=`expr $N + 1`
done
