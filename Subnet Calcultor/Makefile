pkg = subnet
source = $(pkg)/Subnet.java $(pkg)/Calculator.java
jc = javac

classfiles = $(source:.java=.class)

all: $(classfiles)

%.class: %.java
	$(jc) $<

clean:
	rm -f $(pkg)/*.class
