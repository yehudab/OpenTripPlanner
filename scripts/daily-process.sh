#!/bin/bash
today=$(date +%Y-%m-%d)
logs="/var/otp/logs"
gtfs="/var/otp/gtfs"
graphs="/var/otp/graphs"
otpSource="/home/yehuda/OpenTripPlanner"
logfile="$logs/daily-process-$today.log"
echo "Start daily processing at $(date)" | tee $logfile
if [ ! -f $gtfs/local-gtfs.txt ] ; then
    touch $gtfs/local-gtfs.txt
fi
curl ftp://199.203.58.18/ -o $gtfs/remote-gtfs.txt
if diff $gtfs/local-gtfs.txt $gtfs/remote-gtfs.txt  >/dev/null ; then
    echo No new GTFS file| tee -a $logfile
else
    echo Found new GTFS file from $(awk '{print $1, $2}' /var/otp/gtfs/remote-gtfs.txt)| tee -a $logfile
    echo Start fetching GTFS file at $(date)| tee -a $logfile
    wget -O "$gtfs/israel-public-transportation-$today.zip" -o "$logs/wget-$today.log" ftp://199.203.58.18/israel-public-transportation.zip
    if [ ! -s "$gtfs/israel-public-transportation-$today.zip" ] ; then
        cat "$logs/wget-$today.log"| tee -a $logfile
        echo "Could not fetch GTFS file !!"| tee -a $logfile
    else
        echo End fetching GTFS file at $(date)| tee -a $logfile
        ls -l "$gtfs/israel-public-transportation-$today.zip"| tee -a $logfile
        rm  $gtfs/local-gtfs.txt
        mv  $gtfs/remote-gtfs.txt $gtfs/local-gtfs.txt
        ln -sf "$gtfs/israel-public-transportation-$today.zip" "$gtfs/israel-public-transportation.zip"
        echo Stopping tomcat server| tee -a $logfile
        /sbin/service tomcat7 stop
        echo Building new Graph.obj file| tee -a $logfile
        if [ -h $graphs/Graph.obj ] ; then
            rm $graphs/Graph.obj
        fi
        builder=$otpSource/opentripplanner-graph-builder
        java -Xmx5800M -jar $builder/target/graph-builder.jar $builder/samples/graph-config-il-site.xml >& "$logs/graph-builder-$today.log"
        head "$logs/graph-builder-$today.log"| tee -a $logfile
        echo "..."| tee -a $logfile
        tail  "$logs/graph-builder-$today.log"| tee -a $logfile
        ls -l "$graphs/Graph.obj"| tee -a $logfile
        if [ ! -s "$graphs/Graph.obj" ] ; then
            mv $graphs/Graph.obj "$graphs/Graph-$today-failed.obj"
            echo "Build of Graph.obj failed !!"| tee -a $logfile
            echo "Using latest good graph:"| tee -a $logfile
            ls -l $graphs/Graph-latest-good.obj | tee -a $logfile
        else
            mv $graphs/Graph.obj "$graphs/Graph-$today.obj"
            if [ -h $graphs/Graph-latest-good.obj ] ; then
                rm $graphs/Graph-latest-good.obj
            fi
            ln -s "$graphs/Graph-$today.obj" $graphs/Graph-latest-good.obj
            echo "Build of Graph.obj succeeded" | tee -a $logfile
        fi
        ln -s $graphs/Graph-latest-good.obj $graphs/Graph.obj
        echo Starting tomcat server| tee -a $logfile
        /sbin/service tomcat7 start
    fi
fi

echo "End daily processing at $(date)"| tee -a $logfile
