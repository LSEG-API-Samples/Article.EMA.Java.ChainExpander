
package com.thomsonreuters.platformservices.ema.utils.chain;

import com.thomsonreuters.ema.access.OmmConsumer;
import com.thomsonreuters.platformservices.ema.utils.chain.FlatChain.ChainCompleteFunction;
import com.thomsonreuters.platformservices.ema.utils.chain.FlatChain.ChainErrorFunction;
import com.thomsonreuters.platformservices.ema.utils.chain.FlatChain.ElementAddedFunction;
import com.thomsonreuters.platformservices.ema.utils.chain.FlatChain.ElementChangedFunction;
import com.thomsonreuters.platformservices.ema.utils.chain.FlatChain.ElementRemovedFunction;

interface ChainRecordContext
{
    OmmConsumer getOmmConsumer();
            
    String getName();
    
    String getServiceName();
    
    boolean getWithUpdates();
    
    SummaryLinksToSkipByDisplayTemplate getSummaryLinksToSkipByDisplayTemplate();
    
    int getNameGuessesCount();

    ElementAddedFunction getElementAddedFunction();
    
    ElementChangedFunction getElementChangedFunction();
    
    ElementRemovedFunction getElementRemovedFunction();
    
    ChainCompleteFunction getChainCompleteFunction();
    
    ChainErrorFunction getChainErrorFunction();
    
}
