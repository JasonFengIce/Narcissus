package cn.ismartv.voice.core.filter;

public class word_filter_result
{
	public word_filter_result( int s, int e, int t )
	{
		start = s;
		end   = e;
		tag   = t;
	}
	
	public int start;
	public int end;
	public int tag;
}