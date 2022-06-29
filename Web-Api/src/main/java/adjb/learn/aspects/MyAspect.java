package adjb.learn.aspects;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import adjb.learn.dao.DaoException;

@Aspect
@Component
public class MyAspect {

	// This is an advice method
	// syntax: (? means optional)
	// "execution(modifier? return-type? method-pattern(arg-type, arg-type, ..))"
	@Before("execution(* adjb.learn.dao.ProductDao.*(..))")
	public void logBeforeCalling(JoinPoint joinPoint) {
		System.out.println("Asspect is writing to logger method name: " + joinPoint.getSignature().getName());
		System.out.println("Args are " + Arrays.toString(joinPoint.getArgs()));
	}

	@Around("execution(* adjb.learn.dao.ProductDao.getProductsByPriceRange(Double, Double))")
	public Object fixMaxBeforeMin(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		Object[] args = proceedingJoinPoint.getArgs();
		double min = (Double)args[0], max = (Double)args[1];

		if (min > max) {
			args = new Object[] {max, min};
		}

		return proceedingJoinPoint.proceed(args);
	}
	
	@AfterThrowing(throwing = "ex", pointcut = "execution(* adjb.learn.dao.ProductDao.*(..))")
	public void convertToDaoException(Throwable ex) throws DaoException {
		throw new DaoException(ex);
	}
}
