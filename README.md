# ❗아직 동시성 이슈에 대해 구현을 하지 못하였으니 공부하는 느낌으로 작성해보겠습니다.

### 참조 사이트 : https://velog.io/@mooh2jj/%EB%A9%80%ED%8B%B0-%EC%8A%A4%EB%A0%88%EB%93%9C%EC%9D%98-%EB%8F%99%EC%8B%9C%EC%84%B1-%EC%9D%B4%EC%8A%88

# 동시성 이슈란?
* 스레드는 cpu의 한 단위이다
* 멀티 스레드 
  * 한 코어에서 여러 스레드를 이용해서 번갈아 작업을 처리하는 방식이다
  * 공유하는 영역이 많아 프로세스 방식보다 context switcing(작업전환) 오버헤드가 작아, 메모리 리소스가 상대적으로 적다는 장점
  * 자원을 공유해서 단점도 존재
    * 동시성 이슈
    * 스레드가 동시에 하나의 자원을 공유하고 있기 때문에 같은 자원을 두고 경쟁상태(raceCondition) 같은 문제가 발생하는 것


## 동시성
* 동시에 실행되는 것처럼 보이는 것
* 싱글 코에서 멀티 스레드를 동작시키기 위한 방식으로, 멀티 태스킹을 위해 여러 개의 스레드가 번갈아가면서 실행되는 성질
* 멀티 스레드로 동시성을 만족시킬 수 있는 것이지 동시성과 멀티 스레드는 연관이 없다.
  * 반례로 코틀린은 싱글스레드에서 코루틴을 이용하여 동시성을 만족할 수 있다
  * 코루틴 Coroutine
    * 싱글 스레드에서도 루틴(routine) 이라는 단위(맥락상 함수와 동일)로 루틴간 협력이 가능
    * 동시성 프로그래밍을 지원하고 비동기 처리를 쉽게 도와주는 개념
  * 싱글 코에서 멀티 스레드를 이용해 동시성을 구현하는 일부 케이스 대한 내용
    * 멀티 코어에서 멀티 스레드를 이용하여 동시성을 만족할 경우에는 실제 물리적 시간으로 동시에 실행됨

## 병렬성
* 싱레졸 동시에 실행되는 것
* 멀티 코어에서 멀티 스레드를 동작시키는 방식으로 1개 이상의 스레드를 포함하는 각 코어들이 동시에 실행되는 성질
* 부분적으로만 맞는 내용이다. 병렬성의 핵심음 물리적인 시간에 동시에 수행되는 것이지 멀티 코어에 포커스가 맞춰져서는 안 된다. 그 예로 네트워크 상의 여러 컴퓨터에게 분산작업을 요청하는 분산 컴퓨팅이 있음


## 스레드 안정성(Thread safe) 란?
* 여러 스레드가 작동하는 환경에서도 문제 없이 동작하는 것을 스레드가 안전하다고 말함
* 동시성 이슈를 해결하고 일어나지 않는다면 Thread safe 하다고 함


## 동시성을 제어하는 방법

1) 암시적 Lock (synchronized)
   * 동시서을 해결하는데 가장 간단하면서 쉬운 방법은 Lock을 걸어 버리는 것임
   * 문제가 된 메서드, 변수에 각각 synchronized 라는 키워드를 넣는 것이다.
   ``` 
   Class Count {
    private int count;
    public synchronized int view() { return count++;}
   
   Class Count {
    private Integer count = 0;
    public int view() {
        synchronized (this.count) {
            return count++;
        }
   }
   ```
2) 명시적 Lock
* synchronized 키워드 없이 명시적으로 ReentrantLock을 사용하는 방법
* 해당 Lock의 범위를 메서드 내부에서 한정하기 어렵거나, 동시에 여러 Lock을 사용하고 싶을 때 사용한다.
* 직접 Lock 객체를 생성하여 사용한다.
   ```
    public class CountingTest {
    public static void main(String[] args) {
        Count count = new Count();
        for (int i = 0; i < 100; i++) {
            new Thread(){
                public void run(){
                    for (int j = 0; j < 1000; j++) {
                        count.getLock().lock();
                        System.out.println(count.view());
                        count.getLock().unlock();
                    }
                }
            }.start();
        }
    }
}
```
public class CountingTest {
    public static void main(String[] args) {
        Count count = new Count();
        for (int i = 0; i < 100; i++) {
            new Thread(){
                public void run(){
                    for (int j = 0; j < 1000; j++) {
                        count.getLock().lock();
                        System.out.println(count.view());
                        count.getLock().unlock();
                    }
                }
            }.start();
        }
    }
}
class Count {
    private int count = 0;
    private Lock lock = new ReentrantLock();
    public int view() {
        return count++;
    }
public Lock getLock(){
    return lock;
    };
}
```
  
3) 스레드 안전한 객체 사용
* concurrent 패키지는 각종 Thread safe collection을 제공 (ex.ConcurrentHashMap)
* Concurrent 패키지
  * Concurrent 패키지에 존재하는 컬랙션들은 락을 사용할 때 발생하는 성능 저하를 최소한으로 만든다.
  * 락을 여러 개로 분할하여 사용하는 Lock Striping 기법을 사용하여 동시에 여러 스레드가 하나의 자원에 접근하더라도 동시성 이슈가 발생하지 않도로고 도와주는 것
```
class Count {
    private AtomicInteger count = new AtomicInteger(0);
    public int view() {
            return count.getAndIncrement();
    }
}
```
* ConcurrentHashMap
  * ConcurrentHashMap은 내부적으로 여러 개의 락을 가지고 해시값을 이용해 이러한 락을 분할하여 사용
  * 분할 락을 사용하여 병렬성과 성능이라는 2마리 토끼를 모두 잡은 컬랙션
  * 내부적으로 여러 락을 사용, 일반적인 map을 사용할 때처럼 구현하면 내부적으로 알아서 락을 자동으로 사용해줌

4) 불편 객체 Immutable Instance
* Thread safe programming 하는 방법 중 효과적인 방법은 불변 객체를 만드는 것
* 불변객체의 대표적인 예 String. 불편 객체는 락을 걸 필요가 없다.
* 내부적인 상태가 변하지 않으니 여러 스레드에서 동시에 참조해도 동시성 이슈가 발생하지 않는 것
* 즉 불변 객체는 언제나 Thread-safe 하다
* 생성자로 모든 상태 값을 생성할 때 세팅하고, 객체의 상태를 변화시킬 수 있느 부분을 모두 제거해야 함
* 가장 간단한 방법은 Setter를 만들지 않는 것
* 내부 상태가 변하지 않도록 모든 변수를 final로 선언하는 것도 있음
> final 키워드를 쓰면 무조건 초기화를 해야 한다
* 데이터 자체를 Stream() 안에서 캡슐화 해서 결과를 도출하는 것도 불변화 시키는 방법 (함수형 프로그램을 사용하는 이유)



타이밍 하면서 읽고 코드도 직접 적었지만 아직까지는 배움이 부족함을 느낀다 열심히 공부해야겠다..
재미있긴 재미있네 ㅎㅎ