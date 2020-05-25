package org.mockito.configuration;

// need to disable cache
// however I have got a ClassCastException when running all tests :( when mocking GiftAggregateRoot
// https://gist.github.com/bsharathchand/a37d26a47b383c6fcaf1
// https://stackoverflow.com/questions/33828339/classcastexception-exception-when-running-2-roboelectic-test-classes-with-power
public class MockitoConfiguration extends DefaultMockitoConfiguration {

    // @see the documentation in IMockitoConfiguration about ClassCastException
    @Override
    public boolean enableClassCache() {
        return false;
    }

}