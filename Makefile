
JCC = javac

JFLAGS = -g

JVM = java

MAIN = gatorLibrary

FILE = 

default: gatorLibrary.class


gatorLibrary.class: gatorLibrary.java
		$(JCC) $(JFLAGS) gatorLibrary.java

clean: 
		rm -f *.class