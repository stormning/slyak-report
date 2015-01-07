package com.slyak;

/**
 * .
 * <p/>
 *
 * @author <a href="mailto:stormning@163.com">stormning</a>
 * @version V1.0, 2015/1/6
 */
public class SpringSupport {
    public SpringSupport() {
        SpringContext.getInstance().autowire(this);
    }
}
