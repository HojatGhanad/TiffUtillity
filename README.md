# TiffUtillity
This small java application calculates the average over all the frames in a tiff file and generates a single output averaged frame.
</br>
<h1>How to use:</h1>
</br>
1- Build the project using maven
</br>
2- Put your tiff file in the same folder as the jar file
</br>
3- Run the tiff-0.1.jar file using java -jar
</br>
4- The app will request the tiff file name, enter it.
</br>
5- Done, averaged tiff file will be created beside the original file with an avg- prefix.
</br>
* You may face heap memory exception for huge tiff files, give more ram to the app to handle this issue using -Xmx with java command.(e.g. java -Xmx10G -jar tiff-0.1.jar )  
