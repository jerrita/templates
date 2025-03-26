ProjectName = MyProj
TopLevel = LedTop
BenchTarget = ./test/proj_tb.v

OS := $(shell uname -s)
ifeq ($(OS),Darwin)
    ICELINK_DIR = /Volumes/iCELink
else ifeq ($(OS),Windows_NT)
    ICELINK_DIR = E:
else
    $(error Please specify ICELINK_DIR: $(OS))
endif
$(warning iCELink path: $(ICELINK_DIR))

## Internal Veriable
bench_out = ./build/bench.vvp
pcf_file = ./res/io.pcf

all: bench

.PHONY: spinal bench build
spinal:
	sbt "runMain myproj.GenVerilog"

bench:
	@echo "WARN: Maybe you need make spinal first."
	@mkdir -p build
	# iverilog -DBENCH -y ./build/spinal -y /opt/homebrew/share/yosys/ice40 -o ${bench_out} ${BenchTarget}
	# vvp -n $(bench_out)
	@echo "WIP"

build:
	@mkdir -p build
	@yosys -p "hierarchy -top ${TopLevel}; synth_ice40 -json build/sys.json" ./build/spinal/*.v
	@nextpnr-ice40 \
		--up5k \
		--package sg48 \
		--json build/sys.json \
		--pcf $(pcf_file) \
		--asc build/pnr.asc > build/nextpnr.log 2>&1
	@cat build/nextpnr.log | grep -A15 utilisation
	@icepack build/pnr.asc build/prog.bin

flash: build/prog.bin
	@if [ -d '$(ICELINK_DIR)' ]; \
    then \
		cp build/prog.bin $(ICELINK_DIR); \
    else \
		echo "iCELink not found"; \
		exit 1; \
    fi