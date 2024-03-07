#! /usr/bin/perl

package main;
use strict;
use FileHandle;

my $NL = "\n";

open(LOG,">logfile") || die "Cannot create log file.";
LOG->autoflush(1);
STDOUT->autoflush(1);

sub plog {
  print LOG scalar localtime();
  print LOG " " . $_[0] . "\n";
}

plog ("Logfile created.");

sub commit {
  select(undef, undef, undef, 0.25);
}

# synchronize with OVM
plog("Synchronizing with Ovm");

WAIT_FOR_OVM:while() {

  my $rin;
  my $win;
  my $ein;
  
  $rin = $win = $ein = '';
  vec($rin,fileno(STDIN),1) = 1;
  $ein = $rin | $win;
  
  my $nfound;
  $nfound = select($rin, $win, $ein, 1);
  plog("Select returned ".$nfound);

  if ($nfound == 0) {
    plog("Sending handshake.");
    print "HANDSHAKE" . $NL;
    next WAIT_FOR_OVM;

  } else {
    my $response = <STDIN>;
    plog("Received response: X".$response."X");

    if ( $response =~ /Awaiting remote handshake/) {
      last WAIT_FOR_OVM; 
    }
  }
}

# send command line arguments
plog("Sending CMDLINE-FOLLOWS");

print "CMDLINE-FOLLOWS".$NL;
commit();
my $response = <STDIN>;
plog("Reponse for CMDLINE-FOLLOWS was ".$response);

plog("Sending arguments");
 
print "" . ($#ARGV + 1) . $NL;
commit();
$response = <STDIN>;
plog("Reponse for argument count was ".$response);

foreach my $arg (@ARGV) {
  plog("Sending argument: X".$arg."X");
  print $arg . $NL;
  plog("Sent argument, waiting for confirmation... ".$arg);  
  commit();
  my $response = <STDIN>;
  plog("Reponse for argument ".$arg." was ".$response);
}

# get and parse outputs
plog("Waiting for outputs");

open (OUT,">outfile") || die "Cannot open out file";
OUT->autoflush(1);

my $savingDetector = 0;
my $savingDetectorRelease = 0;
my $savingSimulator = 0;

PARSING:while (<STDIN>) {
  print OUT;
  
  
  if (m!=====DETECTOR-STATS-START-BELOW====!) {
    open (DETECTOR, ">detector.rin") || die "Cannot open detector stats file";
    DETECTOR->autoflush(1);
    $savingDetector = 1;
    plog("Detector starts start");
    next PARSING;
  }

  if (m!=====SIMULATOR-STATS-START-BELOW====!) {
    open (SIMULATOR, ">simulator.rin") || die "Cannot open simulator stats file";
    SIMULATOR->autoflush(1);
    $savingSimulator = 1;
    plog("Simulator starts start");    
    next PARSING;
  }  


  if (m!=====DETECTOR-RELEASE-STATS-START-BELOW====!) {
    open (DETECTOR_RELEASE, ">release.rin") || die "Cannot open detector release stats file";
    DETECTOR_RELEASE->autoflush(1);
    $savingDetectorRelease = 1;
    plog("Detector release stats start");    
    next PARSING;
  }

  if (m!=====DETECTOR-STATS-END-ABOVE====!) {
    close (DETECTOR) || die "Cannot close detector stats file";
    $savingDetector = 0;
    plog("Detector stats end");    
    next PARSING;
  }

  if (m!=====SIMULATOR-STATS-END-ABOVE====!) {
    close (SIMULATOR) || die "Cannot close simulator stats file";
    $savingSimulator = 0;
    plog("Simulator stats end");    
    next PARSING;
  }


  if (m!=====DETECTOR-RELEASE-STATS-END-ABOVE====!) {
    close (DETECTOR_RELEASE) || die "Cannot close detector release stats file";
    $savingDetectorRelease = 0;
    plog("Detector release stats end");    
    next PARSING;
  }

  if ($savingSimulator) {
    print SIMULATOR;
    next PARSING;
  }
  
  if ($savingDetector) {
    print DETECTOR;
    next PARSING;
  }
  
  if ($savingDetectorRelease) {
    print DETECTOR_RELEASE;
    next PARSING;
  }
  
  if (m!Detector is finished, processed all frames.!) {
    last PARSING;
  }

}

plog("Benchmark is over. Shutting down.");

close(OUT);
close(LOG)

# done

