package io.github.scyptnex.lcalc.transformer;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TestTransformEvent {

    @Test
    public void stupidMandatoryCoverage(){
        assertThat(TransformationEvent.TransformType.values().length, is(3));
        assertThat(TransformationEvent.TransformType.valueOf("ALPHA"), is(TransformationEvent.TransformType.ALPHA));
        assertThat(TransformationEvent.TransformType.valueOf("BETA"), is(TransformationEvent.TransformType.BETA));
        assertThat(TransformationEvent.TransformType.valueOf("ETA"), is(TransformationEvent.TransformType.ETA));
    }

}
