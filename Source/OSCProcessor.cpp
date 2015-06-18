//
//  OSCProcessor.cpp
//  Plugin Host
//
//  Created by Carl Bussey on 18/06/2015.
//
//

#include "OSCProcessor.h"
#include "JuceHeader.h"

void OSCProcessor::processBlock(juce::MidiBuffer& rMidiMessages, int blockSize)
{
    // @TODO: DO IT!
    
    while (0 /*getNextOSCEvent(rFrequency, rTimeStamp)*/)
    {
        int midiNote = frequencyToMidiNote(0 /*rFrequency*/);
        
        if (midiNote != m_midiNote)
        {
            if (m_midiNote != 1)
            {
                MidiMessage m = MidiMessage::noteOff(1, m_midiNote);
                rMidiMessages.addEvent(&m, 0/*rFrequency*/, 0/*rTimeStamp*/);
            }
            
            if (midiNote != -1)
            {
                MidiMessage m = MidiMessage::noteOn(1, m_midiNote, (uint8)100 /*fixed*/);
                rMidiMessages.addEvent(&m, 0/*rFrequency*/, 0/*rTimeStamp*/);
            }
            
            m_midiNote = midiNote;
        }
    }
}

int OSCProcessor::frequencyToMidiNote(float frequency)
{
    return (frequency > 8.f /*C1*/) ? round(69 + 12 * log2(frequency/440.f)) : -1;
}