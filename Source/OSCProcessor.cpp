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
    if (!m_queue)
    {
        return;
    }
    
    size_t size = m_queue->size();
    
    int refTimestamp;
    if (size)
    {
        refTimestamp = m_queue->front().timestamp;
    }

    for (size_t i = 0; i < size; ++i)
    {
        PitchEvent evt = m_queue->front();
        m_queue->pop();
        
        int midiNote = frequencyToMidiNote(evt.pitch);
        
        if (midiNote != m_midiNote)
        {
            int sampleNumber = ((evt.timestamp - refTimestamp) / 1e-9 * m_fs);
            
            if (m_midiNote != -1)
            {
                MidiMessage m = MidiMessage::noteOff(1, m_midiNote);
                rMidiMessages.addEvent(m, sampleNumber);
            }
            
            if (midiNote != -1)
            {
                MidiMessage m = MidiMessage::noteOn(1, midiNote, (uint8)127 /*fixed*/);
                rMidiMessages.addEvent(m, sampleNumber);
            }
            
            m_midiNote = midiNote;
        }
    }
}

int OSCProcessor::frequencyToMidiNote(float frequency)
{
    return (frequency > 8.f /*C1*/) ? round(69 + 12 * log2(frequency/440.f)) : -1;
}