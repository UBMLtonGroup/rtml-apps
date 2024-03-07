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

public class RealtimeThread extends Thread implements Runnable {

  protected javax.realtime.RealtimeThread realThread;
  protected Runnable logic = null;

  public RealtimeThread( SchedulingParameters scheduling ) {

    realThread = new javax.realtime.RealtimeThread( scheduling.getRealParameters(), 
      null,
      null,
      null,
      null,
      this );
  }

  public void deschedulePeriodic() {
    realThread.deschedulePeriodic();
  }

  public void schedulePeriodic() {
    realThread.schedulePeriodic();
  }

  public void setReleaseParameters( ReleaseParameters release ) {
    realThread.setReleaseParameters( release.getRealParameters() );
  }

  protected RealtimeThread() {
  }

  protected void initialize ( javax.realtime.RealtimeThread realThread, Runnable logic ) {
    this.realThread = realThread;
    this.logic = logic;
  }
  
  public static boolean waitForNextPeriod() {
    return javax.realtime.RealtimeThread.waitForNextPeriod();
  }
  
  public void start() {
    realThread.start();
/*    if (logic != null) {
      this.start();
    }*/
  }
  
  public void run() {
    logic.run();   // this should be overriden when logic == this
    
/*    try {
      realThread.join(); // needed to 
    } catch ( InterruptedException e ) {
    }    */
  }
  
/* no way, join is final
  public void join() {
    realThread.join();
  }
*/

  public void joinReal() throws InterruptedException {
    realThread.join();
  }
}
