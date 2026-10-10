def remove_duplicates_and_sort(file_path):
    try:
        # Read the content of the file and store unique lines in a set
        with open(file_path, 'r') as file:
            lines = set(file.readlines())

        # Write back the unique lines to the same file
        with open(file_path, 'w') as file:
            file.writelines(lines)

        # Read the content of the file again for sorting
        with open(file_path, 'r') as file:
            # Split each line into values x and y
            lines = [line.strip().split(', ') for line in file]

        # Sort the lines based on x, then by y if x is identical
        lines.sort(key=lambda pair: (int(pair[0]), int(pair[1])))

        # Write the sorted lines back to the file
        with open(file_path, 'w') as file:
            for pair in lines:
                file.write(', '.join(pair) + '\n')

        print(f"Duplicates removed and file sorted based on 'x' and 'y' in {file_path}")

    except FileNotFoundError:
        print(f"File '{file_path}' not found.")
    except Exception as e:
        print(f"An error occurred: {e}")

# Replace 'no_gos.txt' with the actual path to your file
remove_duplicates_and_sort('no_gos.txt')
