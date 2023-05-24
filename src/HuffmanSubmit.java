import java.util.*;
import java.io.*;
public class HuffmanSubmit implements Huffman {
	
	//Encodes input file using Huffman encoding and writes it to an output file, generates frequency file
	public void encode(String inputFile, String outputFile, String freqFile) {
		BinaryOut writer=new BinaryOut(outputFile);
		BinaryIn reader=new BinaryIn(inputFile);
		Map<Character, Integer> frequencyMap=new HashMap<Character, Integer>();
		Map<Character, String> encodingMap=new HashMap<Character, String>();
		
		//Reads from input file and generates frequencies of each character
		while(!reader.isEmpty()) {
			char c = reader.readChar();
			frequencyMap.put(c, frequencyMap.containsKey(c) ? frequencyMap.get(c) + 1 : 1);
		}
		
		//Builds tree and encoding map which contains binary conversions of each character
		TreeNode root=buildTree(frequencyMap);
		encodingMap=buildEncodingMap(encodingMap, root, ""); 
		
		reader=new BinaryIn(inputFile);
		
		//Reads in from input file again and writes Huffman code conversion to output file
		while(!reader.isEmpty()) {
			char c=reader.readChar();
			for(int i=0; i<encodingMap.get(c).length(); i++) {
				writer.write(encodingMap.get(c).charAt(i)=='1');
			}
		}
		
		writer.flush();
		writer.close();
		
		//Generates list of frequencies and then writes them to frequency file 
		try {
			FileWriter freqWriter=new FileWriter(freqFile);
			String freqList="";
			for(char c:frequencyMap.keySet()) {
				freqList+=toBinary((int)c)+": "+frequencyMap.get(c)+"\n";
			}
			
			freqWriter.write(freqList);
			freqWriter.flush();
			freqWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Decodes input file using huffman decoding
	public void decode(String inputFile, String outputFile, String freqFile){
		BinaryIn reader=new BinaryIn(inputFile);
		BinaryOut writer=new BinaryOut(outputFile);
		Scanner freqFileReader=null;
		
		Map<Character, Integer> frequencyMap = new HashMap<Character, Integer>();
		
		try {
			freqFileReader=new Scanner(new File(freqFile));
	    	} catch (FileNotFoundException e) {
	        	System.out.println("File not found");
	        	System.exit(-1);
	    	}
		
		//Goes through frequency file and converts it to frequency map so it can build the tree
		while(freqFileReader.hasNextLine()) {
            		String line=freqFileReader.nextLine();
            		String[] freqs=line.split(": ");
	        	int code=Integer.parseInt(freqs[0], 2);
	        	int freq=Integer.parseInt(freqs[1]);
	        	frequencyMap.put((char)(code&0xFF), freq);
        	}
		
		freqFileReader.close();
		
		TreeNode root=buildTree(frequencyMap);
		TreeNode currNode=root;
		
		//Checks each bit and if it's false, moves to left of tree, moves right if true
		while(!reader.isEmpty()) {
			boolean bit=reader.readBoolean();
			
			if(bit) {
				currNode=currNode.right;
			}
			else {
				currNode=currNode.left;
			}
			
			if(currNode.isLeaf()) {
				writer.write(currNode.chr);
				currNode=root;
			}
		}
		
		writer.close();
   }
   
   //Builds map of characters and their huffman codes
   private Map<Character,String> buildEncodingMap(Map<Character, String> map, TreeNode n, String prefix) {
		if(n.isLeaf()) {
			map.put(n.chr, prefix);
		}
		else {
			buildEncodingMap(map, n.left, prefix+"0");
			buildEncodingMap(map, n.right, prefix+"1");
		}
		
		return map;
   }
   
   //Builds tree using map of frequencies
   public TreeNode buildTree(Map<Character, Integer> frequencyMap) {
	   PriorityQueue<TreeNode> nodes=new PriorityQueue<TreeNode>();
	   
	   for(Map.Entry<Character, Integer> entry:frequencyMap.entrySet()) {
		   nodes.offer(new TreeNode(entry.getKey(), entry.getValue(), null, null));
	   }
	   
	   //Organizes the tree by 
	   while(nodes.size()>1) {
		   TreeNode left=nodes.poll();
		   TreeNode right=nodes.poll();
		   TreeNode parent=new TreeNode('-', left.freq + right.freq, left, right);
		   nodes.offer(parent);
	   }
	   
	   return nodes.poll();
   }
   
   //converts char (number conversion) into binary for the frequency file
   private String toBinary(int num) {
	   if(num==0) {
		   return "0";
	   }
	   else if(num==1) {
		   return "1";
	   }
	   return toBinary(num/2)+(num%2);
   }
   
   //Represents Huffman Tree data type
   public class TreeNode implements Comparable<TreeNode> {
		private Character chr;
		private int freq;
		private TreeNode left;
		private TreeNode right;
		
		public TreeNode(Character chr, int freq, TreeNode left, TreeNode right) {
			this.chr=chr;
			this.freq=freq;
			this.left=left;
			this.right=right;
		}
		
		public boolean isLeaf() {
			return left==null && right==null;
		}
		
		public int compareTo(TreeNode o) {
			return this.freq - o.freq;
		}
   }
   
   public static void main(String[] args) {
      Huffman  huffman = new HuffmanSubmit();
      huffman.encode("ur.jpg", "ur.enc", "freq.txt");
      huffman.decode("ur.enc", "ur_dec.jpg", "freq.txt");
		// After decoding, both ur.jpg and ur_dec.jpg should be the same. 
		// On linux and mac, you can use `diff' command to check if they are the same. 
   }

}