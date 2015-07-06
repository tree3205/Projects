//: object/ArgumentParser.java

import java.util.HashMap;

/** 
 * Parse the command line arguments, storing into a Hash Map.
 * cmd_args => the Hash Map stores flags as key and values as value
 * 
 * @author yxu66
 */
public class ArgumentParser
{

	private HashMap<String, String> cmd_args;
	private int numFlags;
	private int numArgs;

	/**
	 * Create Hash Map with args.
	 * @param args a String array contains command line arguments.
	 */
	public ArgumentParser(String[] args)
	{
		cmd_args = new HashMap<String, String>();
		parseArgs(args);
		
	}
	
	/**
	 * Test if the target arg is a flag
	 * @param arg the target arg
	 * @return arg is flag => true; otherwise => false.
	 */
	public static boolean isflag(String arg)
	{
		if (arg.startsWith("-"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Test if the target arg is a value
	 * @param arg the target arg
	 * @return arg is value => true; otherwise => false
	 */
	public static boolean isValue(String arg)
	{
		if (arg.startsWith("-"))
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	/**
	 * Check if input command line arguments contains the target flag
	 * @param flag the target flag
	 * @return cmd contains the flag => true; otherwise => false
	 */
	public boolean hasFlag(String flag)
	{
		if (cmd_args.containsKey(flag))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Check if input command line arguments contains the target flag, and with that flag there is a related value
	 * @param flag the target flag
	 * @return cmd contains the (flag, value) pair => true; otherwise => false
	 */
	public boolean hasValue(String flag)
	{
		if (hasFlag(flag))
		{
			if (!cmd_args.get(flag).equals("null"))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Get the value with the related flag
	 * @param flag the target flag
	 * @return the value to the target flag if existed; otherwise, none
	 */
	public String getValue(String flag)
	{
		if (hasFlag(flag))
		{
			return cmd_args.get(flag);
		}
		else
		{
			return "null";
		}
	}
	
	/**
	 * 
	 * @return the number of flags
	 */
	public int numFlags()
	{
		return numFlags;
	}
	
	/**
	 * 
	 * @return the number of arguments
	 */
	public int numArguments()
	{
		return numArgs;
	}

	private void parseArgs(String[] args)
	{
		String open_arg = "-tmp";
		
		for (String arg : args)
		{   
			if (isflag(arg))
			{
				open_arg = arg;
				cmd_args.put(open_arg, "null");
			}
			else
			{
				cmd_args.put(open_arg, arg);
			}
		}
		 
		numFlags = cmd_args.size();
		numArgs = args.length;
		
	}
}