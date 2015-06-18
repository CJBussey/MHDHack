//
//  OSCProcessor.h
//  Plugin Host
//
//  Created by Carl Bussey on 18/06/2015.
//
//

#ifndef __Plugin_Host__OSCProcessor__
#define __Plugin_Host__OSCProcessor__

#include "../JuceLibraryCode/JuceHeader.h"

class OSCProcessor
{
    
public:
    OSCProcessor() {};
    
    void processBlock(MidiBuffer& rMidiMessages);
    
};

#endif /* defined(__Plugin_Host__OSCProcessor__) */
