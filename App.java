package com.computersector.app;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class App {

	public static void main(String[] args) 
	{
		try {
			Files.walkFileTree(Path.of("."), new WalkFileTree(2));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

final class WalkFileTree extends SimpleFileVisitor<Path>
{
	Path initialPath;
	Map<Path,Long> folderSize = new LinkedHashMap<>();
	int initialCount;
	int printLevel;
	
	public WalkFileTree(int printLevel)
	{
		this.printLevel = printLevel;
	}
	
	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
			throws IOException
	{
		Objects.requireNonNull(dir);
		Objects.requireNonNull(attrs);
		
		
		if(dir.getNameCount() == 1)
		{
			initialCount = dir.getNameCount();
			initialPath = dir;
			return FileVisitResult.CONTINUE;
		}
		
		int relativeCount = dir.getNameCount() - initialCount;
		
		if(relativeCount == 1)
		{
			folderSize.clear();
		}
	
		folderSize.put(dir, 0L);
		
		
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
			throws IOException
	{
		Objects.requireNonNull(file);
		Objects.requireNonNull(attrs);
		
		folderSize.merge(file.getParent(), attrs.size(), Long::sum);
//		folderSize.merge(file.getParent(), 0L, (o,n)-> o+= attrs.size());
		
		return FileVisitResult.CONTINUE;
	}

	 @Override
	    public FileVisitResult postVisitDirectory(Path dir, IOException exc)
	        throws IOException
	    {
	        Objects.requireNonNull(dir);
	        if (exc != null)
	            throw exc;
	        
	        if(dir.getNameCount() == 1) return FileVisitResult.TERMINATE;
	        
	        int relativeCount = dir.getNameCount() - initialCount;
	        
	        if(relativeCount == 1)
	        {
	        	folderSize.forEach((path, dirSize)->{
	        		int level = path.getNameCount() - initialCount;
	        		
	        		if(level <= printLevel)
	        		{
	        			System.out.printf("%s[%s] -  %,d bytes%n", "\t".repeat(level-1), path.getFileName(), dirSize);
	        		}
	        	});
	        	
	        }
	        else
	        {
	        	folderSize.merge(dir.getParent(), 0L, (o,n)-> o+= folderSize.get(dir));
	        }
	        
	        return FileVisitResult.CONTINUE;
	    }

}