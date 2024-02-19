pkg = echo
source = $(pkg)/TCPServer.java $(pkg)/TCPClient.java $(pkg)/UDPServer.java $(pkg)/UDPClient.java
jc = javac

classfiles = $(source:.java=.class)

all: $(classfiles)

%.class: %.java
	$(jc) $<

clean:
	rm -f $(pkg)/*.class
