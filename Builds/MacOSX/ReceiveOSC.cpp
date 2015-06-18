/*
    Example of two different ways to process received OSC messages using oscpack.
    Receives the messages from the SimpleSend.cpp example.
*/

#include "ReceiveOSC.h"
#include <fstream>


ReceiveOSC::ReceiveOSC():
    Thread("OscListener Thread"),
    incomingPort(PORT),
    m_ipEndpoint(IpEndpointName::ANY_ADDRESS, incomingPort),
    s(m_ipEndpoint, this),
    m_pitches(std::make_shared<std::queue<PitchEvent>>())
{
    std::cout << m_ipEndpoint.port << std::endl;
    
    DBG("Now called the Constructor");
}

void ReceiveOSC::ProcessMessage(const osc::ReceivedMessage& m,
    const IpEndpointName& /*remoteEndpoint*/)
{
  /*  std::ofstream outfile;
    outfile.open("output.txt",  std::fstream::in | std::fstream::out | std::fstream::app);
    outfile << m.AddressPattern() << std::endl;*/

    auto args = m.ArgumentStream();
    auto path = m.AddressPattern();

    float pitch = 0;

    if (std::strcmp(path, "/pitch") == 0 )
    {
        args >> pitch >> osc::EndMessage;
        //outfile << x << " " << y << " " << z << std::endl;
    }
 
    
    PitchEvent event;
    
//    event.timestamp = std::chrono::time_point_cast<std::chrono::nanoseconds>(std::chrono::high_resolution_clock::now()));;
    event.pitch = pitch;
    
    std::cout << pitch << std::endl;
    
    m_pitches->push(event);
    
    
}

