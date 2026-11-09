package com.computersector.app;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Objects;

public class Main {

	public static void main(String[] args) 
	{	
		try 
		{
			Files.walkFileTree(Path.of("."), new FileWalker(1));
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

}


final class FileWalker extends SimpleFileVisitor<Path>
{
	
	LinkedHashMap<Path, Long> directoryTree;
	int treeDepth;
	Path initialPath;
	int initialNameCount;
	int relativeNameCount;
	
	FileWalker(int treeDepth)
	{
		directoryTree = new LinkedHashMap<Path, Long>();
		this.treeDepth = treeDepth;
	}
	
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
	        throws IOException
	{
		Objects.requireNonNull(dir);
		Objects.requireNonNull(attrs);
		
		if(dir.getNameCount() != 1)
			directoryTree.put(dir, 0L);
		

		return FileVisitResult.CONTINUE;
	}
	
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
            throws IOException
    {
    	Objects.requireNonNull(file);
    	Objects.requireNonNull(attrs);
    	
    	directoryTree.put(file, attrs.size());
    	
    	if(file.getNameCount() == 2)
    	{
    		
    	}
    	else
    	{
        	directoryTree.merge(file.getParent(), 0L, (o,n) -> o+= attrs.size()); // here adding cd
    	}
    


    	return FileVisitResult.CONTINUE;
    }
    
    
    public FileVisitResult postVisitDirectory(Path dir, IOException exc)
            throws IOException
        {
            Objects.requireNonNull(dir);
            if (exc != null)
                throw exc;
            
            
            if((relativeNameCount = dir.getNameCount() - 1) == 1)
            {
            	directoryTree.forEach((path, foldersize) ->{

            		if((path.getNameCount() - 1) <= treeDepth)
            		{
            			int level = path.getNameCount() - 1-1;
            			FileTime dateTime;
            			LocalDateTime moddT = null;
            			
            			try {
							dateTime = Files.getLastModifiedTime(path);
							
							moddT = LocalDateTime.ofInstant(dateTime.toInstant(),  ZoneId.systemDefault());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
            			
            			System.out.printf("%tD %tT %s %s [%s] %,d bytes %n" ,moddT,moddT,
            					Files.isDirectory(path)? "<DIR>": "\t", "\t".repeat(level), path.getFileName(), foldersize);
            		}

            	});
            	
            	directoryTree.clear();
            }
            else
            {
            	Long folderSize = directoryTree.get(dir);
            	directoryTree.merge(dir.getParent(), 0L, (o,n)-> o+= folderSize);
            }
            
            return FileVisitResult.CONTINUE;
        }
}



















