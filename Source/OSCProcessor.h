//
//  OSCProcessor.h
//  Plugin Host
//
//  Created by Carl Bussey on 18/06/2015.
//
//

#ifndef __Plugin_Host__OSCProcessor__
#define __Plugin_Host__OSCProcessor__

#include "PitchEvent.h"

#include <queue>
#include <memory>

namespace juce {
    class MidiBuffer;
}

class OSCProcessor
{
    
public:
    
    void processBlock(juce::MidiBuffer& rMidiMessages, int blockSize);
    void setOSCEventQueue(const std::shared_ptr<std::queue<PitchEvent>> queue) { m_queue = queue; }
    void setFs(float fs) { m_fs = fs; }
    
private:
    int m_midiNote = -1;
    
    int frequencyToMidiNote(float frequency);
    std::shared_ptr<std::queue<PitchEvent>> m_queue;
    float m_fs;
    
};

#endif /* defined(__Plugin_Host__OSCProcessor__) */
