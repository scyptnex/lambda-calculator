package io.github.scyptnex.lcalc.transformer;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TestTransformEvent {

    @Test
    public void stupidMandatoryCoverage(){
        TransformationEvent.TransformType.values();
        TransformationEvent.TransformType.valueOf("ALPHA");
    }

}
