/**
 * MediAI – Client-Side Vanilla JavaScript Utilities
 * Minimal script for browser micro-interactions and chart initialization
 */

document.addEventListener('DOMContentLoaded', function () {
  // 1. Password visibility toggle
  const toggleBtn = document.getElementById('togglePasswordBtn');
  const passwordInput = document.getElementById('passwordInput');
  if (toggleBtn && passwordInput) {
    toggleBtn.addEventListener('click', function () {
      const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
      passwordInput.setAttribute('type', type);
      const icon = toggleBtn.querySelector('i');
      if (icon) {
        icon.classList.toggle('bi-eye');
        icon.classList.toggle('bi-eye-slash');
      }
    });
  }

  // 2. Fast Demo Presets for AI Diagnosis
  window.loadDiagnosisPreset = function (presetType) {
    const symptomsInput = document.getElementById('symptoms');
    const durationInput = document.getElementById('symptomDuration');
    const historyInput = document.getElementById('medicalHistory');
    const conditionsInput = document.getElementById('existingConditions');

    if (!symptomsInput) return;

    if (presetType === 'cardiac') {
      symptomsInput.value = 'Dull retrosternal chest tightness and mild pressure after climbing stairs, lasting 3-4 minutes and resolving upon resting. Occasional palpitations.';
      if (durationInput) durationInput.value = '4 days, intermittent';
      if (historyInput) historyInput.value = 'Stage 1 Hypertension on Lisinopril 10mg daily';
      if (conditionsInput) conditionsInput.value = 'Hypertension';
    } else if (presetType === 'migraine') {
      symptomsInput.value = 'Unilateral throbbing right-side headache with zig-zag flashing light aura, nausea, and severe sensitivity to sound and screen light.';
      if (durationInput) durationInput.value = '18 hours';
      if (historyInput) historyInput.value = 'Episodic tension headaches';
      if (conditionsInput) conditionsInput.value = 'Migraine with typical aura';
    } else if (presetType === 'respiratory') {
      symptomsInput.value = 'Persistent dry cough, wheezing when inhaling, slight breathlessness during brisk walking.';
      if (durationInput) durationInput.value = '1 week';
      if (historyInput) historyInput.value = 'Mild seasonal pollen allergy';
      if (conditionsInput) conditionsInput.value = 'Allergic Bronchitis';
    }
  };
});
