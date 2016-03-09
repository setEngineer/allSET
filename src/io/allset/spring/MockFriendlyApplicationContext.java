package io.allset.spring;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
* <p>To effectively unit test you need to mock certain services/objects. However those
* services are looked up the Spring Factory at run-time. Example: you are unit testing
* object A. Lets say A depends on B. B depends on C. i.e. A --> B --> C.
*
* <p>Now C is the service that you need to mock. There are two options to inject mocked-C
* object in the object model.
*
* <p><b>a. Bean Definition in XML</b>
*
* <p>You can define mocked-C object in the bean definition XML configuration file. And this XML configuration
* file should be the last one to be loaded. So that mocked C object will over-ride the real object's bean definition.
* And at run-time when bean C is looked up mocked-C object will be returned.
*
* <p><b>b. Clever Application Context</b>
*
* <p>CleverApplicationContext extends {@link ClassPathXmlApplicationContext}, which is used in unit tests to wire the beans.
* In CleverApplicationContext you can override the C object with mocked-C object by invoking the
* <b>overrideBean(String beanId, Object object) API</b>. So from this point whenever bean C
* is looked up mocked-C will be returned.
*
* <p><b>Advantages of option 'b' over 'a'</b>
*
* <ol>
*  <li><b>Code Clarity:</b>There is a better code clarity, as you would know that C is being mocked right there in the unit test file. Where as if
*  you are using option a, you would have to dig through each configuration file to find out which objects are mocked.</li>
*  <li><b>Fast:</b>For each unit tests - you want to mock the same object differently. Example if you are mocking a business service, you want
*  to mock different types of responses
*  <ul>
*       <li>Legitimate Response
*       <li>No Data found Response
*       <li>Throwing Exception
*  </ul>
*  Even with in these responses, there could be multiple variations (i.e. throwing NullPointerException, throwing EnhancedException....)
*  For each of these scenarios you don't want to be defining mocking beans in each bean definition xml file. Even if you end up doing it,
*  for each uni test you would have to reload the application context. As all the mocking beans needs to have same bean id.
*  <li><b>Ease of use:</b>If you are using mocking frameworks like Mockito, then you would have to deal with creating Factory methods of
*  Mockito framework to build your object in bean definition XML files, it's becomes little
*  tedious, when compared to coding it in Java.</li>
* <ol>
*
* @author Ram Lakshmanan
*/
public class MockFriendlyApplicationContext extends ClassPathXmlApplicationContext {

	/**
	 * Create a new CleverApplicationContext for bean-style configuration.
	 * @see #setConfigLocation
	 * @see #setConfigLocations
	 * @see #afterPropertiesSet()
	 */
	public MockFriendlyApplicationContext() {
	}

	/**
	 * Create a new CleverApplicationContext for bean-style configuration.
	 * @param parent the parent context
	 * @see #setConfigLocation
	 * @see #setConfigLocations
	 * @see #afterPropertiesSet()
	 */
	public MockFriendlyApplicationContext(ApplicationContext parent) {
		super(parent);
	}

	/**
	 * Create a new CleverApplicationContext, loading the definitions
	 * from the given XML file and automatically refreshing the context.
	 * @param configLocation resource location
	 * @throws BeansException if context creation failed
	 */
	public MockFriendlyApplicationContext(String configLocation) throws BeansException {
		super(configLocation);
	}

	/**
	 * Create a new CleverApplicationContext, loading the definitions
	 * from the given XML files and automatically refreshing the context.
	 * @param configLocations array of resource locations
	 * @throws BeansException if context creation failed
	 */
	public MockFriendlyApplicationContext(String... configLocations) throws BeansException {
		super(configLocations);
	}

	/**
	 * Create a new CleverApplicationContext with the given parent,
	 * loading the definitions from the given XML files and automatically
	 * refreshing the context.
	 * @param configLocations array of resource locations
	 * @param parent the parent context
	 * @throws BeansException if context creation failed
	 */
	public MockFriendlyApplicationContext(String[] configLocations, ApplicationContext parent) throws BeansException {
		super(configLocations, parent);
	}

	/**
	 * Create a new CleverApplicationContext, loading the definitions
	 * from the given XML files.
	 * @param configLocations array of resource locations
	 * @param refresh whether to automatically refresh the context,
	 * loading all bean definitions and creating all singletons.
	 * Alternatively, call refresh manually after further configuring the context.
	 * @throws BeansException if context creation failed
	 * @see #refresh()
	 */
	public MockFriendlyApplicationContext(String[] configLocations, boolean refresh) throws BeansException {
		super(configLocations, refresh);
	}

	/**
	 * Create a new CleverApplicationContext with the given parent,
	 * loading the definitions from the given XML files.
	 * @param configLocations array of resource locations
	 * @param refresh whether to automatically refresh the context,
	 * loading all bean definitions and creating all singletons.
	 * Alternatively, call refresh manually after further configuring the context.
	 * @param parent the parent context
	 * @throws BeansException if context creation failed
	 * @see #refresh()
	 */
	public MockFriendlyApplicationContext(String[] configLocations, boolean refresh, ApplicationContext parent)
			throws BeansException {

		super(configLocations, refresh, parent);
	}


	/**
	 * Create a new CleverApplicationContext, loading the definitions
	 * from the given XML file and automatically refreshing the context.
	 * <p>This is a convenience method to load class path resources relative to a
	 * given Class. For full flexibility, consider using a GenericApplicationContext
	 * with an XmlBeanDefinitionReader and a ClassPathResource argument.
	 * @param path relative (or absolute) path within the class path
	 * @param clazz the class to load resources with (basis for the given paths)
	 * @throws BeansException if context creation failed
	 * @see org.springframework.core.io.ClassPathResource#ClassPathResource(String, Class)
	 * @see org.springframework.context.support.GenericApplicationContext
	 * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader
	 */
	public MockFriendlyApplicationContext(String path, Class clazz) throws BeansException {
		super(path, clazz);
	}

	/**
	 * Create a new CleverApplicationContext, loading the definitions
	 * from the given XML files and automatically refreshing the context.
	 * @param paths array of relative (or absolute) paths within the class path
	 * @param clazz the class to load resources with (basis for the given paths)
	 * @throws BeansException if context creation failed
	 * @see org.springframework.core.io.ClassPathResource#ClassPathResource(String, Class)
	 * @see org.springframework.context.support.GenericApplicationContext
	 * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader
	 */
	public MockFriendlyApplicationContext(String[] paths, Class clazz) throws BeansException {
		super(paths, clazz);
	}

	/**
	 * Create a new CleverApplicationContext with the given parent,
	 * loading the definitions from the given XML files and automatically
	 * refreshing the context.
	 * @param paths array of relative (or absolute) paths within the class path
	 * @param clazz the class to load resources with (basis for the given paths)
	 * @param parent the parent context
	 * @throws BeansException if context creation failed
	 * @see org.springframework.core.io.ClassPathResource#ClassPathResource(String, Class)
	 * @see org.springframework.context.support.GenericApplicationContext
	 * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader
	 */
	public MockFriendlyApplicationContext(String[] paths, Class clazz, ApplicationContext parent)
			throws BeansException {

		super(paths, clazz);
	}

    private Map<String, Object> localContext = new HashMap<>();

    public void overrideBean(String beanId, Object object) {

        localContext.put(beanId, object);
    }

    @Override
    public Object getBean(String beanId) {

        if (localContext.get(beanId) != null) {

                return localContext.get(beanId);
        }

        return super.getBean(beanId);
    }

    @Override
    public Object getBean(String beanId, Class classType) {

        Object bean = getBean(beanId);
        if (bean != null) {

                return bean;
        }

        return super.getBean(beanId);
    }

    @Override
    public Object getBean(String beanId, Object... args) {

        Object bean = getBean(beanId);
        if (bean != null) {

                return bean;
        }

        return super.getBean(beanId, args);
    }

}