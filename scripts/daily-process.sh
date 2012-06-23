#!/bin/bash
today=$(date +%Y-%m-%d)
logs="/var/otp/logs"
gtfs="/var/otp/gtfs"
graphs="/var/otp/graphs"
otpSource="/home/yehuda/OpenTripPlanner"
logfile="$logs/daily-process-$today.log"
echo "Start daily processing at $(date)" > $logfile
if [ ! -f $gtfs/local-gtfs.txt ] ; then
    touch $gtfs/local-gtfs.txt
fi
curl ftp://199.203.58.18/ -o $gtfs/remote-gtfs.txt
if diff $gtfs/local-gtfs.txt $gtfs/remote-gtfs.txt  >/dev/null ; then
    echo No new GTFS file >> $logfile
else
    echo Found new GTFS file from $(awk '{print $1, $2}' /var/otp/gtfs/remote-gtfs.txt) >> $logfile
    echo Start fetching GTFS file at $(date) >> $logfile
    wget -O "$gtfs/israel-public-transportation-$today.zip" -o "$logs/wget-$today.log" ftp://199.203.58.18/israel-public-transportation.zip
    if [ ! -s "$gtfs/israel-public-transportation-$today.zip" ] ; then
        cat "$logs/wget-$today.log" >> $logfile
        echo "Could not fetch GTFS file !!" >> $logfile
    else
        echo End fetching GTFS file at $(date) >> $logfile
        ls -l "$gtfs/israel-public-transportation-$today.zip" >> $logfile
        rm  $gtfs/local-gtfs.txt
        mv  $gtfs/remote-gtfs.txt $gtfs/local-gtfs.txt
        ln -sf "$gtfs/israel-public-transportation-$today.zip" "$gtfs/israel-public-transportation.zip"
        echo Stopping tomcat server >> $logfile
        /sbin/service tomcat7 stop
        echo Building new Graph.obj file >> $logfile
        if [ -h $graphs/Graph.obj ] ; then
            rm $graphs/Graph.obj
        fi
        builder=$otpSource/opentripplanner-graph-builder
        java -Xmx5200M -jar $builder/target/graph-builder.jar $builder/samples/graph-config-il-site.xml >& "$logs/graph-builder-$today.log"
        head "$logs/graph-builder-$today.log" >> $logfile
        echo "..." >> $logfile
        tail  "$logs/graph-builder-$today.log" >> $logfile
        ls -l "$graphs/Graph.obj" >> $logfile
        if [ ! -s "$graphs/Graph.obj" ] ; then
            echo "Build of Graph.obj failed !!" >> $logfile
        else
            mv $graphs/Graph.obj "$graphs/Graph-$today.obj"
            ln -s  "$graphs/Graph-$today.obj" $graphs/Graph.obj 
        fi
        echo Starting tomcat server >> $logfile
        /sbin/service tomcat7 start
    fi
fi

echo "End daily processing at $(date)" >> $logfile
