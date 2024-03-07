
#
# For RTEMS, use "make TARGET=rtems"
# For Linux, use "make"
#

ifeq "$(TARGET)" "leon3"

# RTEMS/LEON

ifeq "$(FPU)" "yes" 
FPUFLAGS :=
else
FPUFLAGS := -msoft-float
endif # fpu

CC := sparc-rtems-gcc
LD := sparc-rtems-ld
CFLAGS := -DCDC_PERIODIC_DETECTOR -DHARDCODED_ARGUMENTS -O3 -g -mcpu=v8 $(FPUFLAGS) -DRTEMS
LDFLAGS := -lm  $(FPUFLAGS)
FSTARGET := FilesystemImage.o

else # target

# Linux, x86
ifeq "$(PTHREADS)" "yes"
CFLAGS :=  -O2 -g -DCDC_LITTLE_ENDIAN -DBENCHMARKING -DUSE_PTHREADS
LDFLAGS := -lm -lpthread
else
CFLAGS :=  -O2 -g -DCDC_LITTLE_ENDIAN -DBENCHMARKING
LDFLAGS := -lm
endif # pthreads
FSTARGET :=

endif # target


ifeq "$(TARGET)" "linux"


else


endif

SOURCES := $(wildcard *.c)
OBJECTS := $(SOURCES:.c=.o)
DEPENDENCIES := $(SOURCES:.c=.d)

all: cd

include $(DEPENDENCIES)

cd: $(OBJECTS) $(FSTARGET)
	$(CC) -o $@ $^ $(LDFLAGS)

%.d: %.c
	$(CC) $(CFLAGS) -MM -MT '$*.o $*.d' $< > $@

clean: distclean
	rm -f cd

distclean:
	rm -f $(OBJECTS) $(DEPENDENCIES) FilesystemImage.o FilesystemImage

info:
	@echo
	@echo "TARGET = $(TARGET)"

	@echo
	@echo "SOURCES = $(SOURCES)"
	@echo "OBJECTS = $(OBJECTS)"
	@echo "DEPENDENCIES = $(DEPENDENCIES)"

	@echo
	@echo "CC = $(CC)"
	@echo "CFLAGS = $(CFLAGS)"
	@echo "LD = $(LD)"
	@echo "LDFLAGS = $(LDFLAGS)"	
	
#FilesystemImage:	data/mono10.bin data/col.bin data/noi.bin
#	cd data ; tar cf ../FilesystemImage mono10.bin col.bin noi.bin

#FilesystemImage:	data/mono10.bin
#	cd data ; tar cf ../FilesystemImage mono10.bin

#FilesystemImage:	data/lcol.bin
#	cd data ; tar cf ../FilesystemImage lcol.bin

FilesystemImage:	data/frames.bin
	cd data ; tar cf ../FilesystemImage frames.bin

FilesystemImage.o:	FilesystemImage
	$(LD) -r -o $@ -b binary $^
