//
//  OSCProcessor.cpp
//  Plugin Host
//
//  Created by Carl Bussey on 18/06/2015.
//
//

#include "OSCProcessor.h"
#include <cmath>

void OSCProcessor::processBlock(MidiBuffer& rMidiMessages)
{
    //MidiMessage();
    
    
}

int OSCProcessor::frequencyToMidiNote(float frequency)
{
    return (frequency > 0.f) ? round(69 + 12 * log2(frequency/440.f)) : -1;
}