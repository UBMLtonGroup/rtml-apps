#! /bin/bash

N=0
MAX=10

while [ $N -lt $MAX ] ; do

  cat << EOF
add	lineara$N	8-($N/$MAX)-t	0+($N/$MAX)*8	5	ord	ind
add	linearb$N	0+($N/$MAX)*8	8-($N/$MAX)-t	5	ord	ind
EOF
  N=`expr $N + 1`
done
