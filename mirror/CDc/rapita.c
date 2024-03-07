
#include "rapita.h"

uint32* _rapita_output=(uint32*)(rapita_base+0x04);
uint32* _rapita_mask=(uint32*)(rapita_base+0x08);
uint32* _rapita_interruptMask=(uint32*)(rapita_base+0x0c);
uint32  _rapita_flag=0x8000;

int _ri;

void rapita_initialize() {
    *_rapita_mask = 0x0;
    *_rapita_mask = 0xffff;
    *_rapita_interruptMask = 0;
    *_rapita_output = 0;
    _ri=0;

}
