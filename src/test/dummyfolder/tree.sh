
for((i=0; i<10; i++)) 
do
	mkdir $i
	cd $i
	for((j=0; j<20; j++))
	do
		echo "ala ma kota $j" > $j.txt
	done
done
		
