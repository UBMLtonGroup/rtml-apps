/**
 * 
 * Copyright (c) 2001-2010, Purdue University
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   * Neither the name of the Purdue University nor the
 *     names of its contributors may be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *  
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */


package realtime;

public class NoHeapRealtimeThread extends RealtimeThread {

  public NoHeapRealtimeThread(SchedulingParameters scheduling,
    ReleaseParameters release, MemoryParameters memory, MemoryAreaWrapper area,
    ProcessingGroupParameters group, Runnable logic) {
    
      Runnable rtsjLogic = (logic == null) ? this : logic;
    
      if (area == null || area.isReal()) {
      
        initialize( new javax.realtime.NoHeapRealtimeThread(
          (scheduling == null) ? null : scheduling.getRealParameters(),
          (release == null) ? null : release.getRealParameters(),
          (memory == null) ? null : memory.getRealParameters(),
          (area == null) ? null : (javax.realtime.MemoryArea)area.getRealArea(),
          (group == null) ? null : group.getRealParameters(),
          rtsjLogic), logic);    
            
      } else {  // since the area is not real, we are completely in heap,
                // so the heap parameters (and other parameters, potentially)
                // are also in heap, and thus we cannot create non-heap thread
                
        initialize( new javax.realtime.RealtimeThread(
          (scheduling == null) ? null : scheduling.getRealParameters(),
          (release == null) ? null : release.getRealParameters(),
          (memory == null) ? null : memory.getRealParameters(),
          null,
          (group == null) ? null : group.getRealParameters(),
          rtsjLogic), logic);
      }
  }
}
