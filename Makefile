BUILD_DIR = build

PACKAGE = walk
CLASSES = src/*.java src/exception/*.java

build:
	javac -d $(BUILD_DIR) $(CLASSES)

build-to-jar: build
	jar cfe Walk.jar $(PACKAGE).Walk -C $(BUILD_DIR) $(PACKAGE)/
	jar cfe RecursiveWalk.jar $(PACKAGE).RecursiveWalk -C $(BUILD_DIR) $(PACKAGE)/

.SILENT:
clean:
	rm -rf $(BUILD_DIR)
	-rm *.jar 2> /dev/null