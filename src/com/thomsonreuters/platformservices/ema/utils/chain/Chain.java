package com.thomsonreuters.platformservices.ema.utils.chain;

/**
 * The root interface of chains. A <code>Chain</code> represents a specific type 
 * of <code>MarketPrice</code> instruments that contains other instruments names. 
 * From a pure semantic point of view a chain can be considered as a list of 
 * instruments names. A chain is actually made of a chained list of <code>Chain 
 * Records</code> that are <code>MarketPrice</code> instruments that contain 
 * names of other instruments. <code>Chain Records</code> also contain additional
 * fields that link them together (More details about chains data structure are
 * available in <a href="https://developers.thomsonreuters.com/platform-services-work-progress/ema/docs?content=12021&type=documentation_item" target="_blank">this article</a> 
 * available on the  <a href="https://developers.thomsonreuters.com" 
 * target="_blank">Thomson Reuters Developer Portal</a> ).
 * A <code>Chain</code> is an object that subscribes to the underlying 
 * linked list of <code>Chain Records</code> of a given chain and that retrieves 
 * the contained instruments names for you. 
 */
public interface Chain
{
    /**
     * Returns the name of this <code>Chain</code>. This name is used by the 
     * chain to subscribe to its first <code>Chain Record</code>.
     * @return the name of this <code>Chain</code>.
     */
    String getName();
    
    /**
     * Returns the name of the service this <code>Chain</code> is published on. 
     * This service name is used by the chain to subscribe to the underlying 
     * <code>Chain Record</code>.
     * @return the service name used by this <code>Chain</code>.
     */
    String getServiceName();
    
    /**
     * Opens this <code>Chain</code>. Opening a chain starts the subscription 
     * and the decoding of its underlying <code>Chain Record</code>. Once opened, 
     * the chain may start invoking the functional interfaces specified when it 
     * was built. This method has no effect if the chain is already opened. 
     */
    void open();

    /**
     * Closes this <code>Chain</code> and unsubscribes to the underlying <code>
     * Chain Record</code>. Once closed the chain stops invoking the functional
     * interfaces that may have been specified when it was built. This method has
     * no effect if the chain is already closed. 
     */
    void close();

    /**
     * Indicates if this <code>Chain</code> is complete or not. A chain is said
     * complete when it successfully subscribed to its complete chain of <code>
     * Chain Record</code> or if it stopped the subscription phase because of an
     * error. A complete chain may continue to invoke its functional interfaces 
     * if it was built with <code>withUpdates(boolean)</code> set to true.
     * @return true is this <code>Chain</code> is complete. Returns false 
     * otherwise.
     */
    boolean isComplete();
    
    /**
     * Indicates if the name you used to build this <code>Chain</code> corresponds
     * to an actual chain published on the Elektron or Thomson Reuters Enterprise 
     * Platform (TREP) infrastructure. In order to determine if it's the case 
     * the chain must be opened so that it subscribed and decoded it's first 
     * <code>Chain Record</code>. If the chain is not opened, the method 
     * returns <code>false</code>.
     * @return true if the name used to build this chain corresponds to an 
     * instrument that contains all the fields a Chain Record should have. 
     * Returns false otherwise or if the chain is not opened.
     */
    boolean isAChain();    
}
