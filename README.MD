# CrowBar
Java library with implemented access tools based on Reflection, Invoke (MH), Unsafe and ASM
___
## Add to project
Connect to `repo.starfarm.fun` repository
```groovy
repositories {
    maven { url 'https://repo.starfarm.fun/releases' }
}
```

Add dependency `ru.dargen:crowbar-core:CORE-VERSION` - crowbar core with base tools.
And `ru.dargen:crowbar-asm:ASM-VERSION` - crowbar asm module, with fast (direct) accessors and proxies

```groovy
dependencies {
    implementation 'ru.dargen:crowbar-core:CORE-VERSION'
    implementation 'ru.dargen:crowbar-asm:ASM-VERSION'
}
```
___

## Accessors
Simple methods for accessing
```java
var factory = Accessors.invoke();

//constructors
var integerConstructor = factory.openConstructor(Integer.class, int.class);
var number = integerConstructor.newInstance(228);
System.out.println(number.getClass()); //Out: java.lang.Integer

//fields
var stringValueAccessor = factory.openField(String.class, "value", byte[].class);
var string = "Hello, World!";
System.out.println(string); //Out: Hello, World!

stringValueAccessor.setValue(string, new byte[0]);
System.out.println(string); //Out: 
System.out.println(string.length()); //Out: 0

//methods
var stringFormattedAccessor = factory.openMethod(String.class,
        "formatted", String.class, Object[].class);
var text = "Hi, %s!";
text = stringFormattedAccessor.invoke(text, "Jeb");
System.out.println(text); //Out: Hi, Jeb!
```
___

## Proxies
Ease proxy building 
```java
interface SomeCallable {

    void call(String arg);

}

//...

var callable = ProxyBuilder.newBuilder(SomeCallable.class)
    .bindMethod("call", new Class[]{String.class}, (p, m, args) -> {
        System.out.println("Called with " + args[0]);
        return null;
    })
    .bindMethod(Object.class, "toString", new Class[0], (p, m, args) -> "ProxiedCallable")
    .build();

callable.call("DAMN"); //Out: Called with DAMN
System.out.println(callable); //Out: ProxiedCallable
```
___

## Wrapper Proxies
Proxies that implement access methods to fields constructors and methods
```java
@ProxiedClass(String.class)
interface StringAccessor {

    @ConstructorAccessor
    String newString(byte[] bytes);

    @FieldAccessor(inlinedOwner = true) //used owner from compiled proxy
    byte[] getValue();

    @FieldAccessor
    byte[] value(String instance);

    @MethodAccessor(inlinedOwner = true)
    String replace(CharSequence string, CharSequence replacement);

}

//...

var accessor = ProxyWrappers.builtIn()
    .create(StringAccessor.class, Accessors.invoke())
    .wrap("Go play CS?");

System.out.println(accessor.newString(new byte[]{72, 105})); //Out: Hi
System.out.println(Arrays.toString(accessor.getValue())); //Value of inlined object: [71, 111, 32, 37, 115, 63]
System.out.println(Arrays.toString(accessor.value("Hi"))); //Out: [72, 105]
System.out.println(accessor.replace("play CS", "code")); //Out: Go code?
```