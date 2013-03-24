package blog1

import org.objectweb.proactive.api.PALifeCycle

import static blog1.ProActiveMagic.remote

println 'Wizard whitebeard is here: ' + remote { Thread.currentThread().name }
println 'Wally is here: ' + Thread.currentThread().name

PALifeCycle.exitSuccess();
