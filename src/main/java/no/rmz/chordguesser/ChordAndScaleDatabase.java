package no.rmz.chordguesser;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  A very database of chords and scales. Fully in-memory
 *  and indexed only as much as has been necessary
 *  to get halfway decent performance.
 */
public final class ChordAndScaleDatabase {

    private final static Logger LOG = Logger.getLogger(ChordAndScaleDatabase.class.getName());

    private final Map<BitVector, Set<ScaleBean>> scaledb =
            new TreeMap<BitVector, Set<ScaleBean>>();
    
    private final Set<ScaleBean> allScales = new HashSet<ScaleBean>();
    
    
    public int noOfScales() {
        synchronized (scaledb) {
            return allScales.size();
        }
    }

    public void importScale(final ScaleBean entry) {
        synchronized (scaledb) {
          
            final String binary12notes = entry.getBinary12notes();
            if (binary12notes.length() < 1) {
                System.out.println("Scale with no binary representation: " +  entry.toString());
                LOG.log(Level.WARNING, "Scale with no binary representation: {0}", entry.toString());
            } else {
                final BitVector bv = new BitVector(binary12notes);
                final Set<ScaleBean> targetSet;
                if (!scaledb.containsKey(bv)) {
                    targetSet = new HashSet<ScaleBean>();
                    scaledb.put(bv, targetSet);
                } else {
                    targetSet = scaledb.get(bv);
                }
                allScales.add(entry);
                targetSet.add(entry);
            }
        }
    }

    public void  importAllScales (final Collection<ScaleBean> entries) {
        for (final ScaleBean sb: entries) {
            importScale(sb);
        }
    }

    public Set<ScaleBean> getMatchingScales(BitVector cMajorBitvector) {
        return scaledb.get(cMajorBitvector);
    }
}
