package com.course.junit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ StringHelperParametirizedTest.class, StringHelperParametirizedTestMethod2.class })
public class ParameterizedTestSuite {

}
