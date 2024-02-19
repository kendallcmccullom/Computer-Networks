pkg = encryption
source = $(pkg)/Cipher.java $(pkg)/Caesar.java $(pkg)/Vigenere.java $(pkg)/SecretMessage.java
jc = javac

classfiles = $(source:.java=.class)

all: $(classfiles)

%.class: %.java
	$(jc) $<

clean:
	rm -f $(pkg)/*.class
