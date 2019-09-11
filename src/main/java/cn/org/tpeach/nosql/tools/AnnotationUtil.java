package cn.org.tpeach.nosql.tools;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import cn.org.tpeach.nosql.constant.PublicConstant;

/**
 * @author tyz
 * @Title: AnnotationUtil
 * @ProjectName RedisLark
 * @Description: TODO
 * @date 2019-06-23 21:06
 * @since 1.0.0
 */
public class AnnotationUtil {
	private AnnotationUtil() {};
	private final static char PACKSEPARATORCHAR = '.';
	private final static char SEPARATORCHAR = '/';
	/**
	 * 获取类上的注解，获取不到则返回为空
	 *
	 * @param scannerClass
	 *            需要获取的类
	 * @param allowInjectClass
	 *            指定的注解
	 * @return
	 */
	public  static Annotation getClassAnnotation(Class<?> scannerClass, Class<? extends Annotation> allowInjectClass) {
		if (scannerClass.isAnnotationPresent(allowInjectClass)) {
			return scannerClass.getAnnotation(allowInjectClass);
		}
		return null;
	}

	/**
	 * 获取某个类方法上的所有注解
	 *
	 * @param scannerClass：需要获取的类
	 * @param allowInjectClass：
	 * @return
	 */
	public static List<Annotation> getMethodAnnotation(Class<?> scannerClass, Class<? extends Annotation> allowInjectClass) {
		List<Annotation> list;
		final Method[] declaredMethods = scannerClass.getDeclaredMethods();
		if (declaredMethods != null && declaredMethods.length > 0) {
			list = new ArrayList<>(declaredMethods.length);
			for (Method method : declaredMethods) {
				if (method.isAnnotationPresent(allowInjectClass)) {
					list.add(method.getAnnotation(allowInjectClass));
				}
			}
		} else {
			list = new ArrayList<>(0);
		}
		return list;
	}

	/**
	 * 从项目文件获取某包下所有类
	 *
	 * @param packageName
	 *            包名
	 * @param childPackage
	 *            是否遍历子包
	 * @return 类的完整名称
	 * @throws UnsupportedEncodingException
	 */
	public static Set<String> getClassName(String packageName, boolean childPackage) throws IOException {

		Set<String> classList = new HashSet<>();
		if (StringUtils.isNotBlank(packageName)) {
			String packageDirName = packageName.replace(PACKSEPARATORCHAR, SEPARATORCHAR);
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
				// 获取当前目录下面的所有的子目录的url
			Enumeration<URL> urls = loader.getResources(packageDirName);
				URL url = null;
				String protocol = null;
				while (urls.hasMoreElements()) {
					url = urls.nextElement();
					if (url != null) {
						// 得到协议的名称
						protocol = url.getProtocol();
						// 如果当前类型是文件类型
						if ("file".equals(protocol)) {
							// 获取包的物理路径
							String filePath = url.getFile();
							try {
								filePath = URLDecoder.decode(filePath, PublicConstant.CharacterEncoding.UTF_8);
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
//							filePath = filePath.substring(1);
							getFilePathClasses(filePath, packageName, classList, true);
							// 如果当前类型是jar类型
						} else if ("jar".equals(protocol)) {
							// 定义一个JarFile
							JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
							getClassNameByJars(jar, packageDirName, classList, true);
						}
					}

				}
		}
		return classList;

	}

	private static String getPackageAndClassName(String path, String packageName, final Set<String> classList) {
		if (path.endsWith(".class")) {
			path = path.replace(File.separatorChar, PACKSEPARATORCHAR);
			path = path.substring(path.indexOf(packageName), path.lastIndexOf(PACKSEPARATORCHAR));
			if(!classList.contains(path)) {
				classList.add(path);
			}
		}
		return path;
	}

	/**
	 * 从项目文件获取某包下所有类
	 *
	 * @param filePath
	 *            文件路径 类名集合
	 * @param packageName
	 * 			   包名称
	 * @param childPackage
	 *            是否遍历子包
	 * @return 类的完整名称
	 * @throws UnsupportedEncodingException
	 */
	private static void getFilePathClasses(String filePath, String packageName, final Set<String>  classList,boolean childPackage) {
		if (childPackage) {
			LinkedList<File> list = new LinkedList<>();
			File file = new File(filePath);
			list.add(file);
			File[] files;
			while (!list.isEmpty()) {
				file = list.removeFirst();
				if (file.isDirectory()) {
					files = file.listFiles();
					if (!CollectionUtils.isEmptyArray(files)) {
						for (File tmp : files) {
							if (tmp.isDirectory()) {
								list.add(tmp);
							} else {
								getPackageAndClassName(tmp.getPath(), packageName, classList);
							}
						}
					}
				} else {
					getPackageAndClassName(file.getPath(), packageName, classList);
				}

			}
		} else {
			Path dir = Paths.get(filePath);
			DirectoryStream<Path> stream = null;
			try {
				// 获得当前目录下的文件的stream流
				stream = Files.newDirectoryStream(dir, "*.class");
				stream.forEach(p -> getPackageAndClassName(
						String.valueOf(p.getParent()) + File.separator + String.valueOf(p.getFileName()), packageName,
						classList));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 从所有jar中搜索该包，并获取该包下所有类
	 *
	 * @param jar
	 *            jar文件
	 * @param packagePath
	 *            包路径
	 * @param childPackage
	 *            是否遍历子包
	 * @param classList
	 *            存放符合类名信息的classList
	 * @return 类的完整名称
	 */
	private static void getClassNameByJars(JarFile jar, String packagePath, final Set<String> classList,boolean childPackage) {
		if (jar != null) {
			Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				// 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
				JarEntry entry = entries.nextElement();
				String entryName = entry.getName();
				if (entryName.startsWith(packagePath) && entryName.endsWith(".class") && !entry.isDirectory()) {
					if (childPackage) {
						if (entryName.startsWith(packagePath)) {
							entryName = entryName.replace(SEPARATORCHAR, PACKSEPARATORCHAR).substring(0,
									entryName.lastIndexOf(PACKSEPARATORCHAR));
							if(!classList.contains(entryName)) {
								classList.add(entryName);
							}
						}
					} else {
						int index = entryName.lastIndexOf(SEPARATORCHAR);
						String myPackagePath;
						if (index != -1) {
							myPackagePath = entryName.substring(0, index);
						} else {
							myPackagePath = entryName;
						}
						if (myPackagePath.equals(packagePath)) {
							entryName = entryName.replace(SEPARATORCHAR, PACKSEPARATORCHAR).substring(0,
									entryName.lastIndexOf(PACKSEPARATORCHAR));
							if(!classList.contains(entryName)) {
								classList.add(entryName);
							}
						}
					}

				}
			}
		}
	}
}
