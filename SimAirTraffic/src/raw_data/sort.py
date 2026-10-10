def sort_file_by_x(input_file, output_file):
    # Read lines from input file
    with open(input_file, 'r') as f:
        lines = f.readlines()

    # Parse lines, extract x values and sort
    lines_with_x = [(line, float(line.split(',')[1])) for line in lines]
    sorted_lines_with_x = sorted(lines_with_x, key=lambda x: x[1])

    # Write sorted lines to output file
    with open(output_file, 'w') as f:
        for line, _ in sorted_lines_with_x:
            f.write(line)

# Example usage:
input_file = "airport_location.txt"  # Change this to your input file path
output_file = "airport_location_sort.txt"  # Change this to your desired output file path

sort_file_by_x(input_file, output_file)