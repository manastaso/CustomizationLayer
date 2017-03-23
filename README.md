# CustomizationLayer
A prototype implementation of the Customization Layer approach (cf. http://publica.fraunhofer.de/dokumente/N-290562.html)

Build it with maven clean install and then run from the target directory with java -XstartOnFirstThread -jar CustomizationLayer-1.0.jar 

If you are running it on another OS than MacOS you will need to change the SWT library dependency in the POM. 

Apart from that you will probably have to adjust the properties.txt that contains the SVN access properties.
The properties should point to a valid SVN repository alogn with corresponding credentials
