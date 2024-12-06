document.getElementById('generateBtn').addEventListener('click', function() {
    const umlInput = document.getElementById('umlInput').value;
    const classes = parseUML(umlInput);
    generateJavaFiles(classes);
});

function parseUML(uml) {
    const lines = uml.split('\n');
    const classes = {};
    let currentClass = null;

    lines.forEach(line => {
        line = line.trim();
        if (line.startsWith('class ')) {
            const className = line.split(' ')[1];
            currentClass = { name: className, attributes: [], methods: [], relations: [] };
            classes[className] = currentClass;
        } else if (line.includes('--|>')) {
            const [child, parent] = line.split(' --|> ');
            classes[child.trim()].parent = parent.trim();
        } else if (line.includes('-->') || line.includes('<--') || line.includes('--')) {
            const [from, to] = line.split(/-->|<--|--/);
            const relation = { from: from.trim(), to: to.trim(), type: line.includes('-->') ? 'to' : line.includes('<--') ? 'from' : 'both' };
            classes[from.trim()].relations.push(relation);
        } else if (line.includes(':')) {
            const [name, type] = line.split(':');
            currentClass.attributes.push({ name: name.trim(), type: type.trim() });
        }
    });

    return classes;
}

function generateJavaFiles(classes) {
    const classList = document.getElementById('classList');
    classList.innerHTML = '';

    for (const className in classes) {
        const classData = classes[className];
        const javaCode = generateJavaClass(classData);
        const listItem = document.createElement('li');
        listItem.textContent = `Class ${className}`;
        const pre = document.createElement('pre');
        pre.textContent = javaCode;
        listItem.appendChild(pre);
        classList.appendChild(listItem);
    }
}

function generateJavaClass(classData) {
    let code = `public class ${classData.name}`;
    if (classData.parent) {
        code += ` extends ${classData.parent}`;
    }
    code += ' {\n';

    classData.attributes.forEach(attr => {
        code += `    private ${attr.type} ${attr.name};\n`;
    });

    classData.relations.forEach(rel => {
        if (rel.type === 'to') {
            code += `    private ${rel.to} ${rel.to.toLowerCase()};\n`;
        } else if (rel.type === 'from') {
            code += `    private ${rel.from} ${rel.from.toLowerCase()};\n`;
        } else {
            code += `    private ${rel.to} ${rel.to.toLowerCase()};\n`;
        }
    });

    code += '}\n';
    return code;
}