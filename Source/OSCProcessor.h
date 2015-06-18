//
//  OSCProcessor.h
//  Plugin Host
//
//  Created by Carl Bussey on 18/06/2015.
//
//

#ifndef __Plugin_Host__OSCProcessor__
#define __Plugin_Host__OSCProcessor__

namespace juce {
    class MidiBuffer;
}

class OSCProcessor
{
    
public:
    
    OSCProcessor() : m_midiNote(-1) {}
    
    void processBlock(juce::MidiBuffer& rMidiMessages, int blockSize);
    
private:
    int m_midiNote;
    
    int frequencyToMidiNote(float frequency);
    
};

#endif /* defined(__Plugin_Host__OSCProcessor__) */
