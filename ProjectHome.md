wsrf4j2me is an implementation of the Web Services Resource Framework (WSRF) for the Java 2 Microedition (J2ME). It is built on top of the ksoap2 API.

It has been developed with the intent to allow direct access to Grid Web Services (see http://globus.org/toolkit/docs/4.0/common/key/index.html#s-key-overview).

The Web Services Resource Framework consists of the following Web Services specifications:
  * WS-Resource
  * WS-ResourceProperties
  * WS-ResourceLifetime
  * WS-ServiceGroup
  * WS-BaseFaults

In addition, WSRF builds upon:
  * WS-Addressing
  * WS-BaseNotification

Currently, WS-ResourceProperties, WS-Addressing, WS-ResourceLifetime and WS-BaseFaults are fully or partially implemented. WS-ServiceGroup and WS-BaseNotification are not implemented at all so far.

The project contains a sample J2ME application that shows how to use the API and interacts with the CounterService of Globus Toolkit 4.

To my best knowledge, there is no freely available WSRF implementation for J2ME, thus I started implementing this one. It started as a school project and I currently do not have the time to finish it. However, the basics are laid out so that anyone seeking to implement WSRF for J2ME could use this as a basis.
