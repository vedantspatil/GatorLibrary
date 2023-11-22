import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Main class to run the program
public class gatorLibrary {
	public static void main(String[] args) throws IOException {
		// setting a flag 'terminate' to terminate code when duplicate book is found
		int ColorFlipCount = 0;
		List<String> wholeInput = new ArrayList<>();
		String input;
		RedBlackTree rbtObj = new RedBlackTree();

		// setting the path of the output file
		String fileString = args[0].toString()+"_output_file.txt";
		Path opFileName = Path.of(fileString);
		BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileString));
		writer.write("");
		writer.flush();
		
		try {
			// File file = new File("input2.txt");
			File file = new File(args[0]);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String st;
			while ((st = br.readLine()) != null)
				wholeInput.add(st);
			br.close();
		} catch (Exception e) {
			System.out.println("Error "+e);
		}

		try {
			for (String s : wholeInput) {
			input = s;
			String[] arr = input.split("\\(");
			input = arr[0].trim();
			String output;
			switch (input) {
				case "PrintBook":
					String[] bookIds = arr[1].split("\\)");
					// searching the tree for the bookId in the input
					TreeNode book = rbtObj.search(Integer.parseInt(bookIds[0].trim()));
					// if no such book is found, print(0,0,0)
					if (book == null) {
						output= "Book "+ bookIds[0].trim() + " not found in the library\n\n";
						Files.writeString(opFileName, output, StandardOpenOption.APPEND);
					} else {
						// printing the book details of the found book
						output= rbtObj.printNode(book);
						Files.writeString(opFileName, output, StandardOpenOption.APPEND);
					}
					break;
				case "PrintBooks":
					bookIds = arr[1].split("\\)")[0].split(",");
					// printing the book details of the books found in the given range
					ArrayList<TreeNode> nodesInRange = rbtObj.findNodesInRange(rbtObj.getRoot(), Integer.parseInt(bookIds[0]),
							Integer.parseInt(bookIds[1].trim()));
					// if no books are found in the given range
					// Change
					if (nodesInRange.size() == 0) {
						output= "No books are found in the given range\n\n";
						Files.writeString(opFileName, output, StandardOpenOption.APPEND);
					}
					else{
						for(TreeNode node: nodesInRange){
							output= rbtObj.printNode(node);
							Files.writeString(opFileName, output, StandardOpenOption.APPEND);
						}
					}
					break;

				case "InsertBook":
					HashMap<Integer, Integer> oldTree = new HashMap<>();
					rbtObj.inOrderTraversalofTree(rbtObj.getRoot(), oldTree);

					String[] bookDetails = arr[1].split("\\)")[0].split(",");
					book = rbtObj.search(Integer.parseInt(bookDetails[0].trim()));
					
					// if book already exists for the given book number
					if (book != null) {
						output = "Duplicate book\n\n";
						Files.writeString(opFileName, output, StandardOpenOption.APPEND);
						break;
					} else {
						// inserting book into Red Black Tree
						rbtObj.insert(Integer.parseInt(bookDetails[0].trim()),
									 bookDetails[1].trim(),
									 bookDetails[2].trim(),
									 bookDetails[3].trim()
									 );

					}
					HashMap<Integer, Integer> newTree = new HashMap<>();
					rbtObj.inOrderTraversalofTree(rbtObj.getRoot(), newTree);

					for (Map.Entry<Integer,Integer> entry : newTree.entrySet()){
						if(oldTree.containsKey(entry.getKey())){
							if(oldTree.get(entry.getKey()) != entry.getValue()){
								ColorFlipCount++;
							}
						}
					}
					break;

				case "BorrowBook":
					bookDetails = arr[1].split("\\)")[0].split(",");
					book = rbtObj.search(Integer.parseInt(bookDetails[1].trim()));
					//book doesnt exist
					if (book == null) {
						output = "Book does not exist\n\n";
						Files.writeString(opFileName, output, StandardOpenOption.APPEND);
						break;
					}
					//if book availabile
					if(book.availability.toString().trim().equals(new String("\"Yes\""))){
						book.availability = "\"No\"";
						book.borrowedBy = bookDetails[0];
						output = "Book "+ bookDetails[1].trim()+ " borrowed by patron "+ bookDetails[0].trim()+"\n\n";
						Files.writeString(opFileName, output, StandardOpenOption.APPEND);

					}else{
						UserNode newPatron = new UserNode(bookDetails[0].trim(),
											 Integer.parseInt(bookDetails[2].trim()), 
											new Timestamp(System.currentTimeMillis()));
						book.reservationHeap.add(newPatron);
						output = "Book "+ book.bookId +" reserved by patron " + bookDetails[0]+"\n\n";
						Files.writeString(opFileName, output, StandardOpenOption.APPEND);
					}

					break;

				case "ReturnBook":
					bookDetails = arr[1].split("\\)")[0].split(",");
					book = rbtObj.search(Integer.parseInt(bookDetails[1].trim()));
					
					if (book == null) {
						output = "Book does not exist\n\n";
						Files.writeString(opFileName, output, StandardOpenOption.APPEND);
						break;
					}

					output = "Book " + bookDetails[1].trim() + " returned by patron "+ bookDetails[0]+ "\n\n";
					Files.writeString(opFileName, output, StandardOpenOption.APPEND);

					//if book availabile
					
					if(book.reservationHeap.isEmpty()){
						book.availability = "\"Yes\"";
						book.borrowedBy = "None";
					}else{
						UserNode newPatron = book.reservationHeap.poll();
						book.borrowedBy = newPatron.userId;

						output = "Book "+ book.bookId +" allotted to patron " + newPatron.userId +"\n\n";
						Files.writeString(opFileName, output, StandardOpenOption.APPEND);
					}

					break;

				case "DeleteBook":
					oldTree = new HashMap<>();
					rbtObj.inOrderTraversalofTree(rbtObj.getRoot(), oldTree);

					String bookId = arr[1].split("\\)")[0];
					book = rbtObj.search(Integer.parseInt(bookId.trim()));
					if (book == null) {
						output = "Book does not exist\n\n";
						Files.writeString(opFileName, output, StandardOpenOption.APPEND);
						break;
					}

					String displayString = "Book "+bookId+" is no longer available";
					if (book.reservationHeap.size()>=1){
						if(book.reservationHeap.size()>1){
							displayString+=". Reservations made by patrons ";
							// for(UserNode node: book.reservationHeap){
							// 	displayString+=node.userId+" ";
							// }
							for(int i=0; i<book.reservationHeap.size(); i++){
								displayString+=book.reservationHeap.get(i).userId+" ";
							}
							displayString+="have been cancelled!.";
						}else{
							displayString+=". Reservation made by patron "+ book.reservationHeap.poll().userId + " have been cancelled!";
						}
					}
					displayString+="\n";
					output = displayString;
					Files.writeString(opFileName, output, StandardOpenOption.APPEND);
					rbtObj.deleteNode(Integer.parseInt(bookId.trim()));

					newTree = new HashMap<>();
					rbtObj.inOrderTraversalofTree(rbtObj.getRoot(), newTree);
					for (Map.Entry<Integer,Integer> entry : newTree.entrySet()){
						if(oldTree.containsKey(entry.getKey())){
							if(oldTree.get(entry.getKey()) != entry.getValue()){
								ColorFlipCount++;
							}
						}
					}
					break;

				case "FindClosestBook":
					bookId = arr[1].split("\\)")[0];
					ArrayList<Integer> arrayList = rbtObj.printClosest(Integer.parseInt(bookId.trim()));
					for(int book_id: arrayList){
						TreeNode fNode = rbtObj.search(book_id);
						output= rbtObj.printNode(fNode);
						Files.writeString(opFileName, output, StandardOpenOption.APPEND);
					}
					break;

				case "ColorFlipCount":
					output = "Color flip count: "+ ColorFlipCount+"\n\n";
					Files.writeString(opFileName, output, StandardOpenOption.APPEND);
					break;

				case "Quit":
					output = "Program Terminated!!\n\n";
					Files.writeString(opFileName, output, StandardOpenOption.APPEND);
					System.exit(0);
					break;

			}
		}

		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Error Invalid Input"+e);
			System.exit(0);
		}
		// processing the input
		
	}
}