
#ifndef __Plugin_Host__OSCProcessor__
#define __Plugin_Host__OSCProcessor__

#include "../JuceLibraryCode/JuceHeader.h"

class OSCProcessor
{
    
public:
    OSCProcessor() {};
    
    void processBlock(MidiBuffer& rMidiMessages);
    int frequencyToMidiNote(float frequency);
    
};

#endif /* defined(__Plugin_Host__OSCProcessor__) */
